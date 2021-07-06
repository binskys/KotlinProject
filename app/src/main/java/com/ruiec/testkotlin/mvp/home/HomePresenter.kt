package com.ruiec.testkotlin.mvp.home

import android.util.ArrayMap
import android.util.Log
import android.widget.Toast
import com.ruiec.common_library.mvp.BasePresenter
import com.ruiec.common_library.net.ApiErrorModel
import com.ruiec.testkotlin.bean.BannerVipBean
import com.ruiec.testkotlin.bean.HomeBean
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity

/**
 *
 * @author pfm
 * @date 2021/2/2 10:06
 */
class HomePresenter(private val activity: RxAppCompatActivity,private val view: HomeView): BasePresenter(view) {
    private var model : HomeModel =
        HomeModel(this)

    fun getHomeData(){
        val params = ArrayMap<String, Any>()
        params["city"] = "北京"
        params["type"] = 1
        params["identity"] = 1

        model.getHomeData(activity, params)
    }

    fun getBannerVip(from : Int, identity : Int){
        val params = ArrayMap<String, Any>()
        params["is_from"] = from
        params["identity"] = identity

        model.getBannerVip(activity, params)
    }

    fun getHomeDataSuccess(data: HomeBean){
        view.setHomeData(data)
    }

    fun getHomeDataFailure(statusCode: Int, errorModel: ApiErrorModel){
        Toast.makeText(activity, errorModel.msg, Toast.LENGTH_SHORT).show()
        Log.e("reqFailure", errorModel.msg + "  statusCode:" + statusCode)
    }

    fun getBannerDataSuccess(data : BannerVipBean){
        view.setBannerData(data)
    }

    fun checkUpdate(activity: RxAppCompatActivity, isToast: Boolean){
        model.getAppVersion(activity, isToast)
    }
}