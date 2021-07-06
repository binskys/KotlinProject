package com.ruiec.common_library.base

import java.io.Serializable

/**
 *
 * @author pfm
 * @date 2021/1/28 9:00
 */

data class BaseBean<T>(
    var status : Int = 0,
    var code : Int = 0,
    var content : String? = "",
    var msg : String = "",
    var go_token : String? = "",
    var data : T
) : Serializable