package com.ruiec.testkotlin.update

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * @author pfm
 * @date 2021/3/11 9:12
 */
data class VersionBean (
    val app_down_url : String,
    val title : String,
    @SerializedName("abstract")
    val abstractX : String,
    val edition : Int,
    val is_must : Boolean
):Serializable