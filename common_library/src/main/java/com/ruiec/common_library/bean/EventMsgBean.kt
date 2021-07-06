package com.ruiec.common_library.bean

/**
 *
 * @author pfm
 * @date 2021/2/2 17:08
 */
data class EventMsgBean constructor(var type : Int, var msg : String, var bean: Any?){
    constructor(type : Int, msg : String ) : this(type, msg, null)
    constructor(type: Int) : this(type, "", null)
    constructor(type: Int, b: Any?) : this(type, "", b)
}