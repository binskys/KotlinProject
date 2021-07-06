package com.ruiec.common_library.net.download

/**
 *
 * @author pfm
 * @date 2021/3/17 9:04
 */
interface ProgressListener {
    fun onProgress(currentBytes: Long, contentLength: Long, done: Boolean)
}