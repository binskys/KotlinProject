package com.ruiec.testkotlin.update

import java.io.Serializable

/**
 *
 * @author pfm
 * @date 2021/3/11 11:43
 */
data class AppDownloadBean(
    var progress: Int = 0,
    var countSize: Double = 0.0,
    var downloadSize: Double = 0.0
) : Serializable