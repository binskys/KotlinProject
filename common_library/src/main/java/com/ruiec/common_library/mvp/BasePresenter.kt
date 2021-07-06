package com.ruiec.common_library.mvp

import java.lang.ref.WeakReference

/**
 *
 * @author pfm
 * @date 2021/1/28 15:24
 */
open class BasePresenter(view : BaseView) {

    private var mViewRef : WeakReference<BaseView> = WeakReference(view)
    private var mView : BaseView?

    init {
        mView = mViewRef.get()!!
    }


    open fun detachView(){
        mViewRef.clear()
        mView = null
    }
}