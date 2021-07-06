package com.ruiec.common_library.net

/**
 *
 * @author pfm
 * @date 2021/2/1 16:41
 */
interface ReqCallBack<T> {
    fun success(data : T)
    fun failure(statusCode : Int, errorModel : ApiErrorModel)
}