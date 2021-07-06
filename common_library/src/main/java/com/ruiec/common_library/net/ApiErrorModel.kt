package com.ruiec.common_library.net

/**
 *
 * @author pfm
 * @date 2021/1/28 15:44
 */
open class ApiErrorModel(status: Int, msg: String) : Exception(){
    var status : Int = 0
    var msg : String = ""

    init {
        this.status = status
        this.msg = msg
    }
}