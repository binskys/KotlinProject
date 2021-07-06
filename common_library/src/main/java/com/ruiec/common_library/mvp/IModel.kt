package com.ruiec.common_library.mvp

import com.ruiec.common_library.base.BaseBean
import com.ruiec.common_library.net.ApiResponse
import com.ruiec.common_library.net.NetworkScheduler
import com.ruiec.common_library.net.ReqCallBack
import com.ruiec.common_library.net.ResponseTransformer
import com.trello.rxlifecycle4.android.ActivityEvent
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import io.reactivex.rxjava3.core.Observable

/**
 *
 * @author pfm
 * @date 2021/3/11 9:47
 */
open class IModel {
    fun <T>req(activity: RxAppCompatActivity, observable: Observable<BaseBean<T>>, callBack: ReqCallBack<T>){

        observable.compose(ResponseTransformer.handleResult())
            .compose(NetworkScheduler.compose())
            .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : ApiResponse<T>(activity, callBack){})
    }
}