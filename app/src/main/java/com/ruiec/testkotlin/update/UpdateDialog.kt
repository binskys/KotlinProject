package com.ruiec.testkotlin.update

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.content.FileProvider
import com.ayvytr.ktx.ui.onClick
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.hjq.toast.ToastUtils
import com.ruiec.common_library.Constant.RequestIntentConstant
import com.ruiec.testkotlin.R
import com.ruiec.testkotlin.utils.NotificationUtil
import kotlinx.android.synthetic.main.version_update.*
import okhttp3.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.lang.ref.WeakReference
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *版本更新弹窗
 * @author pfm
 * @date 2021/3/11 9:58
 */
class UpdateDialog(context: Context, val bean: VersionBean) : Dialog(context) {
    private val tag = "UpdateDialog"

    /** 是否强制更新  */
    private var isForcedUpdate = false

    /** 安装包  */
    private val apkFile: File? = null

    /** 安装包路径  */
    private val fileName = ""

    /** 是否暂停  */
    private var isDownPause = false

    /** 是否正在下载  */
    private var isDownloading = false

    /** 当前下载进度  */
    private var downloadProgress = 0

    /**
     * 线程池
     */
    private var poolExecutor: ThreadPoolExecutor? = null

    init {
        poolExecutor = ThreadPoolExecutor(
            1, 1, 10, TimeUnit.SECONDS,
            LinkedBlockingQueue(1),
            ThreadPoolExecutor.DiscardOldestPolicy()
        )
        val view = View.inflate(context, R.layout.version_update, null)
        this.setContentView(view)
        this.setCanceledOnTouchOutside(false)
        val dialogWindow: Window? = this.window
        val lp = dialogWindow!!.attributes
        lp.width = (ScreenUtils.getAppScreenWidth() * 0.8).toInt()
        dialogWindow.attributes = lp
        dialogWindow.setGravity(Gravity.CENTER)
        dialogWindow.setBackgroundDrawableResource(R.drawable.dialog_tra_shape)
    }

    fun initDialog(){
        val content: String = bean.abstractX
        val version: String = bean.title
        //是否强制
        //是否强制
        isForcedUpdate = bean.is_must

        if (isForcedUpdate) {
            //强制更新
            ic_update_close.visibility = View.GONE
        } else {
            ic_update_close.visibility = View.VISIBLE
        }
        tv_content.text = content
        if (!TextUtils.isEmpty(version)) {
            val title = " v$version"
            tv_version.text = title
        }
        btn_ok.onClick {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //判断是否需要开启通知栏功能
                if (!NotificationUtil.isNotificationEnabled(context)) {
                    ToastUtils.show(context.getString(R.string.str_update_notification_tip))
                    NotificationUtil.showSettingDialog(context)
                } else {
                    startDownload()
                }
            } else {
                startDownload()
            }
        }
        btn_cancel.onClick {
            this.dismiss()
        }
        tv_download_again.onClick {
            //重试下载
            tv_download_again.visibility = View.GONE
            tv_progress.visibility = View.VISIBLE
            okDownLoad()
        }
    }

    private val handler = MyHandler(this)

    class MyHandler (dialog: UpdateDialog) : Handler() {
        private val reference: WeakReference<*>
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val dialog: UpdateDialog =
                reference.get() as UpdateDialog
            when (msg.what) {
                1 -> {
                    dialog.progressBar.progress = dialog.downloadProgress
                    val str = dialog.downloadProgress.toString() + "%"
                    dialog.tv_progress.text = str
                }
                2 -> {
                    //下载失败
                    dialog.tv_progress.visibility = View.GONE
                    dialog.tv_download_again.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }

        init {
            reference =
                WeakReference<UpdateDialog>(dialog)
        }
    }

    private fun startDownload() {
        if (!isForcedUpdate) {
            this.dismiss()
        }
        //开启下载
        val intent = Intent(context, UpdateService::class.java)
        val bundle = Bundle()
        bundle.putSerializable(RequestIntentConstant.BEAN, bean)
        intent.putExtras(bundle)
        context.startService(intent)
        showDownloadDialog()
    }

    lateinit var downloadingDialog : UpdateDownloadDialog
    fun showDownloadDialog() {
        if(!this::downloadingDialog.isInitialized) {
            downloadingDialog = UpdateDownloadDialog(context)
            downloadingDialog.initDialog()
        }
        downloadingDialog.showDialog()
    }

    private fun okDownLoad() {
        isDownPause = false
        poolExecutor!!.execute(runnable)
    }

    private var runnable = Runnable {
        downloadApk(bean.app_down_url, apkFile!!.path, object : OnDownloadListener {
            override fun onDownloadSuccess(file: File?) {
                isDownloading = false
                LogUtils.i(tag, "下载完成： " + file!!.path)
                //安装应用
                installAPK()
            }

            override fun onDownloading(progress: Int) {
                isDownloading = true
                downloadProgress = progress
                LogUtils.i(tag, "下载进度： $progress")
                handler.sendEmptyMessage(1)
            }

            override fun onDownloadFailed(e: Exception?) {
                isDownloading = false
                if (!isDownPause) {
                    LogUtils.i(tag, "下载失败： " + e!!.message)
                    handler.sendEmptyMessage(2)
                    Looper.prepare()
                    Toast.makeText(context, context.getString(R.string.str_xzsb), Toast.LENGTH_SHORT).show()
                    Looper.loop()
                }
            }

        })
    }

    private var call: Call? = null

    /**
     * okHttp下载问题
     *
     * @param url 下载地址
     * @param filePath 保存到本地的文件
     * @param listener 监听
     */
    private fun downloadApk(
        url: String,
        filePath: String,
        listener: OnDownloadListener
    ) {
        var request: Request? = null
        var file: File? = null
        var accessFile: RandomAccessFile? = null
        try {
            // 储存下载文件的目录
            file = File(filePath)
            //RandomAccessFile断点下载文件
            accessFile = RandomAccessFile(file, "rw")
            //添加请求头 请求从上次文件位置继续下载
            val range = String.format("bytes=%s-", file.length().toString())
            request = Request.Builder().url(url)
                .addHeader("range", range).build()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            //java.lang.IllegalArgumentException: unexpected url: 192.168.20.20:8017/storage/file/180516/1526439043.apk
        }
        if (request == null) {
            return
        }
        val client = OkHttpClient()
        val finalAccessFile = accessFile
        val finalFile = file
        call = client.newCall(request)
        call!!.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onDownloadFailed(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                var `is`: InputStream? = null
                val buf = ByteArray(2048 * 2)
                var len: Int
                try {
                    `is` = response.body()!!.byteStream()
                    //文件总大小 剩余未下载长度+已下载长度
                    val total = response.body()!!.contentLength() + finalFile!!.length()
                    //文件已下载的进度
                    var lastProgress = (finalFile.length() * 100 / total).toInt()
                    //继续下载文件从之前下载长度开始进行
                    finalAccessFile!!.seek(finalFile.length())
                    while (`is`.read(buf).also { len = it } != -1) {
                        finalAccessFile.write(buf, 0, len)
                        val sum = finalFile.length()
                        val progress = (sum * 100 / total).toInt()
                        if (progress > lastProgress) {
                            //当进度改变时去调用通知更新
                            lastProgress = progress
                            listener.onDownloading(progress)
                        }
                        // 下载中更新进度条
                        LogUtils.i("downLoad进度： $progress")
                    }
                    // 下载完成
                    LogUtils.i("downLoad成功： " + finalFile.path)
                    listener.onDownloadSuccess(finalFile)
                } catch (e: java.lang.Exception) {
                    LogUtils.i("downLoad失败： " + e.message)
                    listener.onDownloadFailed(e)
                } finally {
                    try {
                        `is`?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    interface OnDownloadListener {
        /**
         * @param file 下载成功后的文件
         */
        fun onDownloadSuccess(file: File?)

        /**
         * @param progress 下载进度
         */
        fun onDownloading(progress: Int)

        /**
         * @param e 下载异常信息
         */
        fun onDownloadFailed(e: java.lang.Exception?)
    }

    /**
     * apk安装 方法一
     */
    private fun installAPK() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val apkUri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0 O(26)，8.1(27)
            apkUri = FileProvider.getUriForFile(context, context.packageName + ".provider", apkFile!!)
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            //6.0 M(23) //7.0 N(24)；7.1 N(25)；
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            // apkUri = FileProvider.getUriForFile(context, packageString, apkFile);
            apkUri = Uri.fromFile(apkFile)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            apkUri = Uri.fromFile(apkFile)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        }
        context.startActivity(intent)
        //在Circlr+项目中引发华为8.0手机自动更新造成自动安装时解析包失败问题
        // android.os.Process.killProcess(android.os.Process.myPid());
    }
}