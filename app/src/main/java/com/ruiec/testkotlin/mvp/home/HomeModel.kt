package com.ruiec.testkotlin.mvp.home

import android.util.ArrayMap
import com.ruiec.common_library.mvp.BasePresenter
import com.ruiec.common_library.net.ApiErrorModel
import com.ruiec.common_library.net.ReqCallBack
import com.ruiec.testkotlin.bean.BannerVipBean
import com.ruiec.testkotlin.bean.HomeBean
import com.ruiec.testkotlin.mvp.BaseModel
import com.ruiec.testkotlin.net.HttpBuilder
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity

/**
 *
 * @author pfm
 * @date 2021/2/1 16:54
 */
class HomeModel(presenter: BasePresenter) : BaseModel() {
    private var mPresenter : HomePresenter = presenter as HomePresenter

    fun getHomeData(activity : RxAppCompatActivity, params : ArrayMap<String, Any>){
        req(activity, HttpBuilder.instance.getService().getHomeData(params), object :
            ReqCallBack<HomeBean> {
            override fun success(data: HomeBean) {
                mPresenter.getHomeDataSuccess(data)
            }

            override fun failure(statusCode: Int, errorModel: ApiErrorModel) {
                mPresenter.getHomeDataFailure(statusCode, errorModel)
            }
        })
    }

    fun getBannerVip(activity : RxAppCompatActivity, params : ArrayMap<String, Any>){
        req(activity, HttpBuilder.instance.getService().getBannerVip(params), object :
            ReqCallBack<BannerVipBean> {
            override fun success(data: BannerVipBean) {
                mPresenter.getBannerDataSuccess(data)
            }

            override fun failure(statusCode: Int, errorModel: ApiErrorModel) {
                mPresenter.getHomeDataFailure(statusCode, errorModel)
            }
        })
    }
}