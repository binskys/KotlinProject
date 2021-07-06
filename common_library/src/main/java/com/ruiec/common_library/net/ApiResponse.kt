package com.ruiec.common_library.net

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.ruiec.common_library.util.LoadingDialog
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 *
 * @author pfm
 * @date 2021/1/28 15:39
 */
abstract class ApiResponse<T>(private val context:Context, private val callBack: ReqCallBack<T>) : Observer<T>{

    override fun onSubscribe(d: Disposable?) {
        LoadingDialog.show(context)
    }

    override fun onNext(t: T) {
        callBack.success(t)
    }

    override fun onComplete() {
        LoadingDialog.cancel()
    }

    override fun onError(e: Throwable?) {
        LoadingDialog.cancel()
        if(e is HttpException){
            val errorModel : ApiErrorModel = when (e.code()){
                ApiErrorType.INTERNAL_SERVER_ERROR.code ->
                    ApiErrorType.INTERNAL_SERVER_ERROR.getApiErrorModel(context)
                ApiErrorType.BAD_GATEWAY.code ->
                    ApiErrorType.BAD_GATEWAY.getApiErrorModel(context)
                ApiErrorType.NOT_FOUND.code ->
                    ApiErrorType.NOT_FOUND.getApiErrorModel(context)
                else ->
                    otherError(e)
            }
            Toast.makeText(context, errorModel.msg, Toast.LENGTH_SHORT).show()
            callBack.failure(e.code(), errorModel)
            return
        }

        if(e is ApiErrorModel){
            Toast.makeText(context, e.msg, Toast.LENGTH_SHORT).show()
            callBack.failure(700, e)
        }

        val apiErrorType: ApiErrorType = when (e) {  //发送网络问题或其它未知问题，请根据实际情况进行修改
            is UnknownHostException -> ApiErrorType.NETWORK_NOT_CONNECT
            is ConnectException -> ApiErrorType.NETWORK_NOT_CONNECT
            is SocketTimeoutException -> ApiErrorType.CONNECTION_TIMEOUT
            else -> ApiErrorType.UNEXPECTED_ERROR
        }
        val apiErrorModel = apiErrorType.getApiErrorModel(context)
        Toast.makeText(context, apiErrorModel.msg, Toast.LENGTH_SHORT).show()
        callBack.failure(apiErrorType.code, apiErrorModel)
    }

    private fun otherError(e: HttpException) : ApiErrorModel {
        return Gson().fromJson(e.response()?.errorBody()?.charStream(), ApiErrorModel::class.java)
    }
}