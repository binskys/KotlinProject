package com.ruiec.testkotlin.net

import com.ruiec.common_library.base.BaseBean
import com.ruiec.testkotlin.bean.BannerVipBean
import com.ruiec.testkotlin.bean.HomeBean
import com.ruiec.testkotlin.update.VersionBean
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 *
 * @author pfm
 * @date 2021/1/27 18:13
 */
interface ApiService {

    @JvmSuppressWildcards
    @GET("api/new/index")
    fun getHomeData(@QueryMap params:Map<String, Any>) : Observable<BaseBean<HomeBean>>

    /**
     * 首页banner和会员图
     */
    @JvmSuppressWildcards
    @GET("api/new/banner")
    fun getBannerVip(@QueryMap params: Map<String, Any>): Observable<BaseBean<BannerVipBean>>

    @GET("api/app")
    fun getAppVersion(): Observable<BaseBean<VersionBean>>

    @GET("api/app")
    suspend fun getAppVersion2(): BaseBean<VersionBean>

    @GET("api/app")
    fun getAppVersion3(): Call<BaseBean<VersionBean>>
}