package com.ruiec.common_library.net.download

import android.os.Handler
import android.os.Looper
import android.os.Message
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.lang.ref.WeakReference

/**
 *
 * @author pfm
 * @date 2021/3/17 9:13
 */
class ProgressResponseBody(private val body: ResponseBody, val listener: ProgressListener) : ResponseBody() {
    val update : Int = 0x01
    private var bufferedSource:BufferedSource? = null
    private var myHandler =
        MyHandler(
            this
        )

    init {
    }

    companion object{
        private class MyHandler(context: ProgressResponseBody) : Handler(Looper.getMainLooper()){

            private var weakReference : WeakReference<ProgressResponseBody> = WeakReference(context)

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if(msg.what == weakReference.get()!!.update){
                    val model = msg.obj as ProgressModel
                    weakReference.get()!!.listener.onProgress(model.currentBytes, model.contentLength, model.done)
                }
            }
        }
    }

    override fun contentLength(): Long {
        return body.contentLength()
    }

    override fun contentType(): MediaType? {
        return body.contentType()
    }

    override fun source(): BufferedSource {
        if(null == bufferedSource){
            bufferedSource = Okio.buffer(getSource(body.source()))
        }
        return bufferedSource!!
    }

    private fun getSource(source: Source) : Source{
        return object : ForwardingSource(source) {
            var totalBytesRead : Long = 0L
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                //发送消息到主线程，ProgressModel为自定义实体类
                val msg = Message.obtain()
                msg.what = update
                msg.obj = ProgressModel(
                    totalBytesRead,
                    contentLength(),
                    totalBytesRead == contentLength()
                )
                myHandler.sendMessage(msg)
                return bytesRead
            }
        }
    }
}