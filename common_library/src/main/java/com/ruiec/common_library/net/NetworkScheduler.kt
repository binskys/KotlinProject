package com.ruiec.common_library.net

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 *
 * @author pfm
 * @date 2021/1/28 17:10
 */
object NetworkScheduler {
    fun <T> compose() : ObservableTransformer<T, T> {
        return ObservableTransformer{observable ->
            observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}