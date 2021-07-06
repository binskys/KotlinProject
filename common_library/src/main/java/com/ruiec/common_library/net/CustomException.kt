package com.ruiec.common_library.net

import com.google.gson.JsonParseException
import org.json.JSONException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

/**
 *
 * @author pfm
 * @date 2021/1/29 14:46
 */
object CustomException {
    /**
     * 未知错误
     */
    private const val UNKNOWN : Int = 1000

    /**
     * 解析错误
     */
    private const  val PARSE_ERROR : Int = 1001

    /**
     * 网络错误
     */
    private const  val NETWORK_ERROR : Int = 1002

    fun handleException(e : Throwable?) : ApiErrorModel {
        val errorModel : ApiErrorModel?
        if (e is JsonParseException || e is JSONException || e is ParseException) {
            //解析错误
            errorModel = ApiErrorModel(
                PARSE_ERROR,
                e.cause?.message.toString()
            )
            return errorModel
        } else if (e is ConnectException){
            //网络错误
            errorModel = ApiErrorModel(
                NETWORK_ERROR,
                e.cause?.message.toString()
            )
            return errorModel
        } else if (e is UnknownHostException){
            //网络错误
            errorModel = ApiErrorModel(
                NETWORK_ERROR,
                e.cause?.message.toString()
            )
            return errorModel
        } else if (e is SocketTimeoutException){
            //连接超时
            errorModel = ApiErrorModel(
                NETWORK_ERROR,
                "连接超时"
            )
            return errorModel
        } else{
            errorModel = ApiErrorModel(
                UNKNOWN,
                e?.cause?.message.toString()
            )
            return errorModel
        }
    }
}