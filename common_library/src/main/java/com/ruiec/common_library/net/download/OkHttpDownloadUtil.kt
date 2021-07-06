package com.ruiec.common_library.net.download

import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.TextView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.hjq.toast.ToastUtils
import com.ruiec.common_library.Constant.ConfigConstant
import com.ruiec.common_library.R
import com.ruiec.common_library.base.BaseDialog
import com.ruiec.common_library.util.CopyUtil
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 下载工具类
 * @author pfm
 * @date 2021/3/17 8:58
 */
object OkHttpDownloadUtil {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS)
        .readTimeout(10000, TimeUnit.MILLISECONDS)
        .writeTimeout(10000, TimeUnit.MILLISECONDS).build()

    private var dialog: BaseDialog? = null
    private lateinit var path: String


    fun download(context: Context, fileName: String, url: String, callback: MyCallBack){
        val folder = getPublicDiskCacheDir(context, Environment.DIRECTORY_DOWNLOADS)
            .toString() + ConfigConstant.parentFolder
        path = folder + fileName
        if (!FileUtils.isFileExists(folder)) {
            File(folder).mkdirs()
        }
        if (FileUtils.isFileExists(File(path))){
            File(path).delete()
        }
        showPro(context)
        downloadFile(url, object : ProgressListener {
                override fun onProgress(currentBytes: Long, contentLength: Long, done: Boolean) {
                    val progress = (currentBytes * 100 / contentLength).toInt()
                    if (null != dialog) {
                        dialog!!.setText(R.id.dialog_upload_tvProTitle, "$progress/100")
                        dialog!!.setProgressBar(R.id.dialog_upload_progressBar, progress)
                    }
                    if (done) {
                        if (null != dialog) {
                            dialog!!.dismiss()
                        }
                        if (Build.VERSION.SDK_INT >= 29) {
                            val savePath =
                                context.getExternalFilesDir(null)!!.absolutePath + ConfigConstant.parentFolder + fileName
                            val saveFile = File(savePath)
                            if (saveFile.exists() && !saveFile.parentFile!!.exists()) {
                                saveFile.parentFile!!.mkdirs()
                            } else {
                                callback.onFailure()
                                LogUtils.e("下载失败，文件路径有误！")
                            }
                            val b: Boolean = CopyUtil.copyFile(context, path, CopyUtil.getUri(context, savePath))
                            if (b) {
                                //复制成功
                                callback.onSuccess(savePath)
                            } else {
                                //复制失败
                                callback.onFailure()
                                ToastUtils.show(context.getString(R.string.str_xzsb))
                            }
                        } else {
                            callback.onSuccess(path)
                        }
                    }
                }
            },
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //复制失败
                    callback.onFailure()
                    ToastUtils.show(context.getString(R.string.str_xzsb))
                    if (null != dialog) {
                        dialog!!.dismiss()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val `is` = response.body()!!.byteStream()
                    val fos =
                        FileOutputStream(File(path))
                    var len: Int
                    val buffer = ByteArray(2048)
                    while (-1 != `is`.read(buffer).also { len = it }) {
                        fos.write(buffer, 0, len)
                    }
                    fos.flush()
                    fos.close()
                    `is`.close()
                }
            })
    }


    private fun downloadFile(url: String, listener: ProgressListener, callback: Callback){
        //增加拦截器 监听下载进度
        val client = okHttpClient.newBuilder().addInterceptor { chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder().body(
                ProgressResponseBody(
                    response.body()!!,
                    listener
                )
            ).build()
        }.build()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(callback)
    }

    /**
     * 注释描述:获取缓存目录
     *
     * @fileName 获取外部存储目录下缓存的 fileName的文件夹路径
     */
    private fun getPublicDiskCacheDir(context: Context, fileName: String): String? {
        val cachePath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
                //此目录下的是外部存储下的私有的fileName目录
                //SDCard/Android/data/你的应用包名/cache/fileName
                context.externalCacheDir!!.path + "/" + fileName
            } else {
                context.cacheDir.path + "/" + fileName
            }
        val file = File(cachePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        //SDCard/Android/data/你的应用包名/cache/fileName
        return file.absolutePath
    }

    private fun showPro(context: Context){
        if(null == dialog){
            dialog = BaseDialog.Builder(context).setWidth(0.8)
                .canceledOnTouchOutside(false)
                .setView(R.layout.dialog_project_upload_file).build()
            dialog!!.init {
                val tvTip = it.findViewById<TextView>(R.id.tv_tip)
                tvTip.text = context.getString(R.string.str_downloading)
            }
        }
        dialog!!.show()
    }

    interface MyCallBack{
        fun onSuccess(path: String)
        fun onFailure()
    }
}