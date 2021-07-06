package com.ruiec.testkotlin.bean

import java.io.Serializable

/**
 *
 * @author pfm
 * @date 2021/1/28 14:28
 */
data class HomeBean(
    var match : ArrayList<MatchBean>
): Serializable {
    data class MatchBean(
        var banner_type :Int = 0,
        var start_time : String? = "",
        var end_time : String? = "",
        var logo : String? = "",
        var sign_up_num : Int = 0,
        var sponsor_match_id : Int = 0,
        var title : String? = "",
        var click : Int = 0
    ) : Serializable
}