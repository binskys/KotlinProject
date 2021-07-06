package com.ruiec.testkotlin.net

import com.ruiec.common_library.net.RetrofitFactory

/**
 *
 * @author pfm
 * @date 2021/1/27 16:24
 */
open class HttpBuilder private constructor(): RetrofitFactory<ApiService>() {

    private object Holder{
        val INSTANCE = HttpBuilder()
    }

    companion object {
        val instance by lazy { Holder.INSTANCE }
    }

    override fun getApiServiceClass(): Class<ApiService> {
        return ApiService::class.java
    }

    open fun getService() : ApiService {
        return apiService!!
    }

    init {
        init()
    }
}