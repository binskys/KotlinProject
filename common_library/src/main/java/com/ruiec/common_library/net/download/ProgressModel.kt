package com.ruiec.common_library.net.download

/**
 *
 * @author pfm
 * @date 2021/3/17 9:43
 */
data class ProgressModel(var currentBytes: Long, var contentLength: Long, var done: Boolean)