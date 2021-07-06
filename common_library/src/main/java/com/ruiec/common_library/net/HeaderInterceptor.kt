package com.ruiec.common_library.net

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * 头部请求拦截
 * @author pfm
 * @date 2021/1/27 17:44
 */
class HeaderInterceptor : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        val request : Request = chain.request()
            .newBuilder()
            .addHeader("version", "")
            .addHeader("timestamp", "")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("token", "")
            // 数据形式(app or  h5)，形式app需要传app值
            .addHeader("apptype", "app")
            .build()
        val headers = request.headers()
        println("----请求的参数（头）----- $headers")
        return chain.proceed(request)
    }
}