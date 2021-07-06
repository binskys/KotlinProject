package com.ruiec.common_library.net

import com.ruiec.common_library.base.BaseBean
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.functions.Function

/**
 *
 * @author pfm
 * @date 2021/1/29 13:54
 */
object ResponseTransformer {

    fun <T>handleResult() : ObservableTransformer<BaseBean<T>, T> {
        return ObservableTransformer{
                observable ->
                    observable
                        //.onErrorResumeNext(ErrorResumeFunction())
                        .flatMap (ResponseFunction())
        }
    }

    /**
     * 非服务器产生的异常，比如本地无无网络请求，Json数据解析错误等等。
     *
     * @param <T>
     */
    private class ErrorResumeFunction<T> : Function<Throwable, ObservableSource<BaseBean<T>>>{
        override fun apply(t: Throwable?): ObservableSource<BaseBean<T>> {
            error(CustomException.handleException(t))
        }
    }

    /**
     * 服务其返回的数据解析
     * 正常服务器返回数据和服务器可能返回的exception
     *
     * @param <T>
     */
    private class ResponseFunction<T> : Function<BaseBean<T>, ObservableSource<T>> {
        override fun apply(t: BaseBean<T>?): ObservableSource<T> {
            val code = t?.code
            val status = t?.status
            val msg = t?.msg
            return if(code == 200 || status == 200){
                Observable.just(t.data)
            } else{
                Observable.error(
                    ApiErrorModel(
                        status!!,
                        msg!!
                    )
                )
            }
        }
    }
}