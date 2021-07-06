package com.ruiec.testkotlin.mvp.home

import com.ruiec.common_library.mvp.BaseView
import com.ruiec.testkotlin.bean.BannerVipBean
import com.ruiec.testkotlin.bean.HomeBean

/**
 *
 * @author pfm
 * @date 2021/2/2 10:02
 */
interface HomeView : BaseView {
    fun setHomeData(bean : HomeBean)
    fun setBannerData(bean : BannerVipBean)
}