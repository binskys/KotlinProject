package com.ruiec.testkotlin.update

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar

/**
 *
 * @author pfm
 * @date 2021/3/11 14:17
 */
class MySeekBar(context: Context, attrs: AttributeSet, defStyleAttr: Int) : AppCompatSeekBar(context, attrs, defStyleAttr) {
    /**
     * 是否支持拖动进度
     */
    private var touch = false

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    fun setTouch(t: Boolean){
        touch = t
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (touch) {
            super.onTouchEvent(event)
        } else false
    }
}