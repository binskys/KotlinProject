package com.ruiec.testkotlin.widget.head

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.BarUtils
import com.ruiec.testkotlin.R
import kotlinx.android.synthetic.main.head_view.*

/**
 * 头部视图
 * @author pfm
 * @date 2021/2/21 15:12
 */
class MyHeadView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var activity : Activity = context as Activity


    private var barView : View
    private var tvTitle : TextView
    private var ivLeft : ImageView
    private var ivRight1 : ImageView
    private var ivRight2 : ImageView
    init {
        val headView = LayoutInflater.from(context).inflate(R.layout.head_view, this)

        barView = headView.findViewById(R.id.head_status)
        tvTitle = headView.findViewById(R.id.head_tv_title)
        ivLeft = headView.findViewById(R.id.head_iv_back)
        ivRight1 = headView.findViewById(R.id.head_right_ivIcon)
        ivRight2 = headView.findViewById(R.id.head_right_ivIcon2)

        ivLeft.setOnClickListener {
            activity.finish()
        }

        val linearParams = barView.layoutParams
        linearParams.height = BarUtils.getStatusBarHeight()
        barView.layoutParams = linearParams
    }

    fun setTitle(title : String){
        tvTitle.text = title
    }

    fun setRight1ImgAndClick(resourceId : Int, listener : OnClickListener){
        ivRight1.setImageResource(resourceId)
        ivRight1.setOnClickListener { val listener1 = listener }
    }

    fun setRight2ImgAndClick(resourceId : Int, listener : OnClickListener){
        ivRight2.setImageResource(resourceId)
        ivRight2.setOnClickListener { val listener1 = listener }
    }
}