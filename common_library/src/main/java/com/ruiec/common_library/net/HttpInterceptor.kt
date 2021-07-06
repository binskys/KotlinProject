package com.ruiec.common_library.net

import com.ruiec.common_library.util.LogUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author pfm
 * @date 2021/1/27 16:41
 */
open class HttpInterceptor :Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val responseBody = response.body()
        //val contentLength = responseBody?.contentLength()

        val source = responseBody?.source()
        source?.request(Long.MAX_VALUE)
        val buffer = source?.buffer

        val charset = Charset.forName("UTF-8")
//        if(contentLength!! > 0L){
            val result = buffer?.clone()?.readString(charset)
            //保存响应日志
            var logMsg =
            "<!---------响应日志----------->" +
                    "\n接口：" + request.url() +
                    "\n方式：" + request.method() +
                    "\n头部：" + request.headers()
            if("POST" == request.method()){
                logMsg += "\n参数：" + request.body()
            }
            com.blankj.utilcode.util.LogUtils.d(logMsg)
            //打印
            LogUtils.printJson("HTTP响应日志", result!!)
//        }

        return response
    }

    /**
     * 获取当前年月日 时分秒时间
     *
     * @return String
     */
    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        sdf.timeZone = TimeZone.getTimeZone("GMT+8")
        return sdf.format(Date())
    }
}