package com.ruiec.testkotlin.widget.banner

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import com.youth.banner.indicator.BaseIndicator
import kotlin.math.max

/**
 * 首页Banner指示器
 * @author pfm
 * @date 2021/2/25 10:41
 */
open class HomeIndicator(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
    : BaseIndicator(context, attrs, defStyleAttr) {

    private var mNormalRadius = 0
    private var mSelectedRadius = 0
    private var maxRadius = 0
    private var rectF : RectF

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        mNormalRadius = config.normalWidth / 2
        mSelectedRadius = config.selectedWidth / 2
        rectF = RectF()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count = config.indicatorSize
        if (count <= 1) {
            return
        }

        mNormalRadius = config.normalWidth / 2
        mSelectedRadius = config.selectedWidth / 2
        //考虑当 选中和默认 的大小不一样的情况
        //考虑当 选中和默认 的大小不一样的情况
        maxRadius = max(mSelectedRadius, mNormalRadius)
        //间距*（总数-1）+选中宽度+默认宽度*（总数-1）
        //间距*（总数-1）+选中宽度+默认宽度*（总数-1）
        val width =
            (count - 1) * config.indicatorSpace + config.selectedWidth + config.normalWidth * (count - 1)
        setMeasuredDimension(
            width, max(config.normalWidth, config.selectedWidth)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val count = config.indicatorSize
        if (count <= 1) {
            return
        }
        var left = 0f
        for (i in 0 until count) {
            mPaint.color =
                if (config.currentPosition == i) config.selectedColor else config.normalColor
            val indicatorWidth =
                if (config.currentPosition == i) config.selectedWidth else config.normalWidth
            val radius =
                if (config.currentPosition == i) mSelectedRadius else mNormalRadius
            rectF.set(left, 0f, left + indicatorWidth, config.height.toFloat())
            canvas!!.drawRoundRect(rectF, radius.toFloat(), radius.toFloat(), mPaint)
            left += indicatorWidth + config.indicatorSpace.toFloat()
        }
    }
}