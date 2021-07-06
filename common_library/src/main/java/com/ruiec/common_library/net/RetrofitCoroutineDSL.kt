package com.ruiec.common_library.net

import com.ruiec.common_library.base.BaseBean
import retrofit2.Call

/**
 * Retrofit协程接收
 * @author pfm
 * @date 2021/5/28 14:54
 */
class RetrofitCoroutineDSL<T> {
    var api: (Call<BaseBean<T>>)? = null
    internal var onSuccess:((T) -> Unit)? = null
        private set
    internal var onFail:((msg:String, code: Int) -> Unit)? = null
        private set
    internal var onComplete:(() -> Unit)? = null
        private set

    /** 加载成功 */
    fun onSuccess(block: (T) -> Unit){
        this.onSuccess = block
    }

    /** 加载失败 */
    fun onFail(block: (msg:String, code: Int) -> Unit){
        this.onFail = block
    }

    /** 加载完成 */
    fun onComplete(block: () -> Unit){
        this.onComplete = block
    }

    internal fun clean(){
        onSuccess = null
        onFail = null
        onComplete = null
    }


}