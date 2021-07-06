package com.ruiec.common_library.widget.adapter

import android.view.View

/**
 *
 * @author pfm
 * @date 2021/2/3 14:21
 */
open class NoDoubleClickListener : View.OnClickListener{
    private var clickTime = 0L

    override fun onClick(v: View?) {
        val currentTime = System.currentTimeMillis()
        if(currentTime - clickTime > 500){
            clickTime = currentTime
            onNoDoubleClick(v)
        }
    }

    private fun onNoDoubleClick(v: View?) {}
}