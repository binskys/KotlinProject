package com.ruiec.common_library.net

import android.content.Context
import androidx.annotation.StringRes
import com.ruiec.common_library.R

/**
 *
 * @author pfm
 * @date 2021/1/28 16:48
 */
enum class ApiErrorType(val code : Int, @param:StringRes private val messageId : Int) {
    INTERNAL_SERVER_ERROR(500, R.string.service_error),
    BAD_GATEWAY(502, R.string.service_error),
    NOT_FOUND(404, R.string.not_found),
    CONNECTION_TIMEOUT(408, R.string.timeout),
    NETWORK_NOT_CONNECT(499, R.string.network_wrong),
    UNEXPECTED_ERROR(700, R.string.unexpected_error);

    fun getApiErrorModel(context: Context) : ApiErrorModel {
        return ApiErrorModel(
            code,
            context.getString(messageId)
        )
    }
}