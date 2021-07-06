package com.ruiec.common_library.net

import android.util.Log
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *
 * @author pfm
 * @date 2021/1/28 14:48
 */
abstract class RetrofitFactory<T> {
    private val defaultTimeout: Long = 20//超时时间
    open var apiService : T? = null

    fun init () {
        val httpClient = OkHttpClient.Builder()
            //.addNetworkInterceptor(getLogInterceptor())
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(HttpInterceptor())
            .connectTimeout(defaultTimeout, TimeUnit.SECONDS)
            .readTimeout(defaultTimeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
        apiService = Retrofit.Builder().client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
            .baseUrl(ApiConfig.baseUrl)
            .build()
            .create(getApiServiceClass())
    }

    abstract fun getApiServiceClass() : Class<T>

    private fun getLogInterceptor() : HttpLoggingInterceptor{
        val logInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            msg ->
            val segmentSize = 3 * 1024
            val length = msg.length
            if (length <= segmentSize ) {// 长度小于等于限制直接打印
                Log.d("请求数据日志", msg)
            }else {
                var message = msg
                var time = 0
                while (message.length > segmentSize) {// 循环分段打印日志
                    val logContent = message.substring(0, segmentSize)
                    message = message.replace(logContent, "")
                    if(time == 0){
                        Log.d("请求数据日志",""+ logContent)
                    } else{
                        Log.d("请求数据日志",message)
                    }
                    time += 1
                }
                Log.d("请求数据日志",message)// 打印剩余日志
            }
        })
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return logInterceptor
    }
}