package com.ruiec.common_library.net.upload

import java.io.Serializable

/**
 *
 * @author pfm
 * @date 2021/3/17 15:22
 */
data class UploadFileBean (var data: DataBean,
    var status: Int, var msg:String) : Serializable{
    data class DataBean(var filename: String, var filepath:String,
        var filesize:String, var filetime:String, var path_url: String)
}