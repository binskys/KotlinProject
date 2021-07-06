package com.ruiec.testkotlin.update

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.hjq.toast.ToastUtils
import com.ruiec.common_library.Constant.RequestIntentConstant
import com.ruiec.testkotlin.R
import com.ruiec.testkotlin.constant.EventConstant
import com.ruiec.common_library.bean.EventMsgBean
import com.ruiec.testkotlin.utils.FileProviderUtils
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *
 * @author pfm
 * @date 2021/3/11 10:55
 */
class UpdateService : Service(){
    private val tag = "UpdateService"
    private var bean: VersionBean? = null
    private var apkFile: File? = null

    /** 是否正在下载  */
    private var isDownloading = false

    /** 下载文件名  */
    private var fileName: String = ""

    /** 当前下载进度  */
    private var downloadProgress = 0

    /** 是否暂停  */
    private var isDownPause = false

    /** APK大小  */
    private var total: Long = 0

    /**
     * 线程池
     */
    private var poolExecutor: ThreadPoolExecutor? = null

    /** 广播  */
    private var myReceiver: MyReceiver? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        myReceiver = MyReceiver()
        val filter = IntentFilter()
        filter.addAction("com.receiver.update.stop")
        filter.addAction("com.receiver.update.continue")
        filter.addAction("com.receiver.update.cancel")
        registerReceiver(myReceiver, filter)
        super.onCreate()
        poolExecutor = ThreadPoolExecutor(
            1, 1, 10, TimeUnit.SECONDS,
            LinkedBlockingQueue(1),
            ThreadPoolExecutor.DiscardOldestPolicy()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent!!.extras != null) {
            val bundle = intent.extras
            bean = bundle!!.getSerializable(RequestIntentConstant.BEAN) as VersionBean?
            if (isDownloading) {
                ToastUtils.show(R.string.str_updating)
            } else {
                initFileDownloader()
                okDownLoad()
            }
        }
        return START_NOT_STICKY
    }

    private fun okDownLoad() {
        isDownPause = false
        poolExecutor!!.execute(runnable)
    }

    private var runnable = Runnable {
        downloadApk(
            bean!!.app_down_url,
            apkFile!!.path,
            object : OnDownloadListener{
                override fun onDownloadSuccess(file: File?) {
                    isDownloading = false
                    LogUtils.i(tag, "下载完成： " + file!!.path)
                    EventBus.getDefault()
                        .post(
                            EventMsgBean(
                                EventConstant.DOWN_LOAD_FILE_SUCCESS
                            )
                        )
                    UpdateNotionManager.remove()
                    //安装应用
                    installAPK()
                }

                override fun onDownloading(progress: Int) {
                    isDownloading = true
                    downloadProgress = progress
                    LogUtils.i(tag, "下载进度： $progress")
                    val totalSizeM = (total / 1024 / 1024).toDouble()
                    val downloadBean =
                        AppDownloadBean(progress, totalSizeM, totalSizeM * progress / 100)
                    EventBus.getDefault().post(
                        EventMsgBean(
                            EventConstant.UPDATE_APP_PROGRESS,
                            downloadBean
                        )
                    )
                    UpdateNotionManager.notifyUser(
                        ActivityUtils.getTopActivity(),
                        progress,
                        apkFile,
                        0
                    )
                }

                override fun onDownloadFailed(e: Exception?) {
                    isDownloading = false
                    if (!isDownPause) {
                        LogUtils.i(tag, "下载失败： " + e!!.message)
                        EventBus.getDefault()
                            .post(
                                EventMsgBean(
                                    EventConstant.UPDATE_APP_DEFAULT_TOAST
                                )
                            )
                        UpdateNotionManager.notifyUser(applicationContext, downloadProgress, apkFile, 2)
                        Looper.prepare()
                        Toast.makeText(
                            applicationContext,
                            application.getString(R.string.str_update_fail),
                            Toast.LENGTH_SHORT
                        ).show()
                        Looper.loop()
                    }
                }

            })
    }

    private fun initFileDownloader() {
        fileName = getString(R.string.app_name) + ".apk"
        apkFile = File(
            getExternalFilesDir(null), fileName
        )
        deleteApk()
    }

    /** 暂停下载  */
    fun stopDownload() {
        isDownPause = true
        if (null != call) {
            call!!.cancel()
        }
        UpdateNotionManager.notifyUser(applicationContext, downloadProgress, apkFile, 1)
    }

    /** 清理文件  */
    private fun deleteApk() {
        if (apkFile != null && apkFile!!.exists()) {
            val delete = apkFile!!.delete()
            val str = if(delete) "成功" else "失败"
            LogUtils.d("APK清理$str")
        }
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
                    total = response.body()!!.contentLength() + finalFile!!.length()
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

    /**
     * 7.0兼容
     */
    private fun installAPK() {
        if (Build.VERSION.SDK_INT > 23) {
            //7.0以上加入了fileProvider
            startInstallN(applicationContext, apkFile)
        } else {
            startInstall(applicationContext, apkFile)
        }
    }

    /**
     * android7.x
     *
     * @param apkFile 文件路径
     */
    private fun startInstallN(mContext: Context?, apkFile: File?) {
        //参数1 上下文, 参数2 在AndroidManifest中的android:authorities值, 参数3  共享的文件
        var context = mContext
        if (context == null) {
            context = ActivityUtils.getTopActivity()
        }
        val apkUri = FileProvider.getUriForFile(context!!, context.packageName + ".provider", apkFile!!)
        val install = Intent(Intent.ACTION_VIEW)
        //由于没有在Activity环境下启动Activity,设置下面的标签
        install.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        // 仅需改变这一行
        FileProviderUtils.setIntentDataAndType(
            context,
            install,
            "application/vnd.android.package-archive",
            apkUri,
            true
        )
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        install.setDataAndType(apkUri, "application/vnd.android.package-archive")
        context.startActivity(install)
    }

    /**
     * android1.x-6.x
     *
     * @param apkFile 文件的路径
     */
    private fun startInstall(context: Context, apkFile: File?) {
        val install = Intent(Intent.ACTION_VIEW)
        install.setDataAndType(
            Uri.fromFile(apkFile),
            "application/vnd.android.package-archive"
        )
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(install)
    }

    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val action = intent.action
            if (TextUtils.isEmpty(action)) {
                return
            }
            when (action) {
                "com.receiver.update.stop" ->
                    //暂停下载
                    stopDownload()
                "com.receiver.update.continue" -> okDownLoad()
                "com.receiver.update.cancel" -> {
                    //自动更新取消 关闭服务 停止下载
                    EventBus.getDefault()
                        .post(
                            EventMsgBean(
                                EventConstant.DOWN_LOAD_FILE_SUCCESS
                            )
                        )
                    isDownloading = false
                    UpdateNotionManager.remove()
                    deleteApk()
                }
                else -> {
                }
            }
        }
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

    override fun onDestroy() {
        unregisterReceiver(myReceiver)
        stopSelf()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        poolExecutor!!.remove(runnable)
        UpdateNotionManager.remove()
    }
}