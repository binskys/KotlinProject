package com.ruiec.testkotlin.bean

import java.io.Serializable

/**
 *
 * @author pfm
 * @date 2021/2/24 11:40
 */
data class BannerVipBean(
    var identity : Int,
    var ad_members :AdMembersBean,
    var banner :List<BannerBean>
):Serializable{
    data class AdMembersBean(
        var id : Int,
        var img : String,
        var link : String,
        var title: String
    ):Serializable
    data class BannerBean(
        var bannerId : Int,
        var file_url : String,
        var id : Int,
        var img : String,
        var type : Int,
        var cate_banner_id : Int,
        var cate_type : Int,
        var product_step : Int
    ):Serializable
}