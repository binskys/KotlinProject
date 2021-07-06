package com.ruiec.common_library.net.upload

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.ruiec.common_library.R
import com.ruiec.common_library.base.BaseDialog
import com.ruiec.common_library.net.ApiConfig
import com.ruiec.common_library.util.MyUtil
import okhttp3.*
import java.io.*
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


/**
 * 上传工具类
 * @author pfm
 * @date 2021/3/17 15:19
 */
object OkHttpUploadUtil {
    /**
     * 读取超时
     */
    private const val writeTimeOut: Long = 10 * 1000

    /**
     * 超时时间
     */
    private const val connectTimeout: Long = 10 * 1000

    private var dialog: BaseDialog? = null

    private var myHandler = MyHandler(this)

    private class MyHandler(context: OkHttpUploadUtil) : Handler(Looper.getMainLooper()){

        private var weakReference : WeakReference<OkHttpUploadUtil> = WeakReference(context)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val pro = msg.arg1
            weakReference.get()!!.setProgress(pro)
        }
    }

    fun uploadFile(context: Context, file:File?, param: String, listener: UploadListener){
        if (file == null || !FileUtils.isFileExists(file)) {
            ToastUtils.showShort(context.getString(R.string.str_wjbcz))
            listener.onFailure()
            return
        }
        showPro(context)
        var result: String?

        val mime = "file/*"

        val builder = MultipartBody.Builder()
        val requestBody = RequestBody.create(MediaType.parse(mime), file)
        builder.addFormDataPart("Filedata",
            MyUtil.getFileNameWithSuffix(file.absolutePath), requestBody)
        builder.addFormDataPart("type", param)
        //设置上传的类型 文件(图片)
        builder.setType(MultipartBody.FORM)
        val multipartBody = builder.build()

        val body = ExMultipartBody(multipartBody,
            object : ExMultipartBody.UploadProgressListener {
                override fun onProgress(contentLength: Long, mCurrentLength: Int) {
                    val pro = mCurrentLength * 100 / contentLength
                    val msg = Message()
                    msg.arg1 = pro.toInt()
                    myHandler.sendMessage(msg)
                }
            })
        val request = Request.Builder().url(ApiConfig.uploadUrl)
            .post(body).build()
        val okHttpClient = OkHttpClient().newBuilder()
            .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                dismissDialog()
                ToastUtils.showShort(context.getString(R.string.str_scsb))
                listener.onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                dismissDialog()
                result = response.body()?.string()
                LogUtils.d("上传返回：$result")
                val bean = Gson().fromJson(result, UploadFileBean::class.java)
                if(bean.status == 1|| bean.status == 200) {
                    listener.onSuccess(bean)
                } else{
                    ToastUtils.showShort(bean.msg)
                    listener.onFailure()
                }
            }
        })
    }

    private fun showPro(context: Context){
        if(null == dialog){
            dialog = BaseDialog.Builder(context).setWidth(0.8)
                .canceledOnTouchOutside(false)
                .setView(R.layout.dialog_project_upload_file).build()
        }
        dialog!!.show()
    }

    fun dismissDialog(){
        if(null != dialog){
            dialog!!.dismiss()
        }
    }

    /**
     * 设置上传进度
     * @param curLen 进度
     * */
    fun setProgress(curLen: Int){
        if(null != dialog){
            dialog!!.setText(R.id.dialog_upload_tvProTitle, "$curLen/100")
            dialog!!.setProgressBar(R.id.dialog_upload_progressBar, curLen)
        }
    }

    interface UploadListener{
        //上传成功
        fun onSuccess(bean: UploadFileBean)
        //上传失败
        fun onFailure()
    }
}