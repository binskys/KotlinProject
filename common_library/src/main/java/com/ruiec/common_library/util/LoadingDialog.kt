package com.ruiec.common_library.util

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ruiec.common_library.R

/**
 *  网络请求加载框
 * @author pfm
 * @date 2021/1/28 16:00
 */
object LoadingDialog {
    private var dialog: Dialog? = null

    fun show(context: Context){
        cancel()
        val view = View.inflate(context, R.layout.dialog_loading, null)
        dialog = Dialog(context, R.style.dialog_style)
        dialog?.setContentView(view)
        dialog?.window!!.setGravity(Gravity.CENTER)
        dialog?.window!!.decorView.setPadding(0, 0 , 0 ,0)
        dialog?.window!!.attributes.width = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window!!.attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        initView(view, context)
        dialog?.show()
    }

    private fun initView(view : View, context: Context){
        val iv = view.findViewById<ImageView>(R.id.iv_loading)
        Glide.with(context).load(R.drawable.gif_loading).into(iv)
    }

    fun cancel(){
        dialog?.dismiss()
    }
}