package com.ruiec.common_library.net.upload

import android.util.Log
import androidx.annotation.Nullable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Okio
import java.io.IOException


/**
 *
 * @author pfm
 * @date 2021/3/18 11:57
 */
class ExMultipartBody(private val requestBody: MultipartBody, val progressListener: UploadProgressListener) : RequestBody() {
    private var mCurrentLength = 0


    @Nullable
    override fun contentType(): MediaType? {
        // 静态代理最终还是调用的代理对象的方法
        return requestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        // 总的长度
        val contentLength = contentLength()
        // 获取当前写了多少数据？BufferedSink Sink就是一个 服务器的 输出流，我还是不知道写了多少数据

        // 又来一个代理 ForwardingSink
        val forwardingSink: ForwardingSink = object : ForwardingSink(sink) {
            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                // 每次写都会来这里
                mCurrentLength += byteCount.toInt()
                progressListener.onProgress(contentLength, mCurrentLength)
                Log.e("TAG", "$contentLength : $mCurrentLength")
                super.write(source, byteCount)
            }
        }
        // 转一把
        val bufferedSink = Okio.buffer(forwardingSink)
        requestBody.writeTo(bufferedSink)
        // 刷新，RealConnection 连接池
        bufferedSink.flush()
    }

    interface UploadProgressListener{
        fun onProgress(contentLength: Long, mCurrentLength: Int)
    }
}