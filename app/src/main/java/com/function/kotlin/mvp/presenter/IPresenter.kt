package com.function.kotlin.mvp.presenter

import android.content.Intent
import com.function.kotlin.mvp.IBaseView
import java.lang.ref.SoftReference

/**
 * @author Zero degree
 * @date 2021/2/3 9:28
 * @功能:
 */

abstract class IPresenter <T: IBaseView>(v:T){
    open var mView: SoftReference<T> = SoftReference(v)

    open fun onCreate(intent: Intent?) {
        mView.get()?.initView();
    }

}