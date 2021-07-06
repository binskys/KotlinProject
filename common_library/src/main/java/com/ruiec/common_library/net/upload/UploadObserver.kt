package com.ruiec.common_library.net.upload

import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

/**
 *
 * @author pfm
 * @date 2021/3/17 18:15
 */
interface UploadObserver<T> : Observer<T> {
    override fun onSubscribe(d: Disposable?) {
        //暂不做处理
    }

    fun onProgress(pro: Int)
}