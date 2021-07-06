package com.ruiec.common_library.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import com.blankj.utilcode.util.ScreenUtils
import com.ruiec.common_library.R

/**
 * Dialog基类
 * @author pfm
 * @date 2021/3/12 10:27
 */
object BaseDialog {
    /***圆角 */
    var circle: Int = R.drawable.dialog_circle_shape

    /***方角 */
    var square: Int = R.drawable.dialog_square_shape

    /***透明 */
    var tra : Int = R.drawable.dialog_tra_shape

    var view : View? = null
    private lateinit var dialog:Dialog

    fun create(): Dialog{
        return dialog
    }

    fun show(){
        if(this::dialog.isInitialized && !dialog.isShowing){
            dialog.show()
        }
    }

    fun dismiss(){
        if(this::dialog.isInitialized && dialog.isShowing){
            dialog.dismiss()
        }
    }

    class Builder(val context: Context){
        private var layoutId: Int = 0
        private var outsideCancel: Boolean = true
        private var gravity: Int = Gravity.CENTER
        private var width: Double = 0.8
        private var height: Double = 1.0
        private var shape : Int = square
        private var listener : DialogInterface.OnDismissListener? = null

        fun setView(layoutId : Int): Builder {
            this.layoutId = layoutId
            return this
        }

        fun canceledOnTouchOutside(b : Boolean) : Builder {
            this.outsideCancel = b
            return this
        }

        fun setWidth(weight : Double) : Builder {
            this.width = weight
            return this
        }

        fun setHeight(weight : Double) : Builder {
            this.height = weight
            return this
        }

        fun setGravity(gravity : Int): Builder {
            this.gravity = gravity
            return this
        }

        fun setShape(shape: Int): Builder {
            this.shape = shape
            return this
        }

        fun setDismissListener(listener: DialogInterface.OnDismissListener): Builder {
            this.listener = listener
            return this
        }

        fun build() : BaseDialog {
            dialog = Dialog(context)
            view = View.inflate(context, layoutId, null)
            dialog.setContentView(
                view!!)
            dialog.setCanceledOnTouchOutside(outsideCancel)
            val dialogWindow: Window? = dialog.window
            val lp = dialogWindow!!.attributes
            lp.width = (ScreenUtils.getAppScreenWidth() * width).toInt()
            if(height < 1){
                lp.height = (ScreenUtils.getAppScreenHeight() * height).toInt()
            }
            dialogWindow.attributes = lp
            dialogWindow.setGravity(gravity)
            dialogWindow.setBackgroundDrawableResource(shape)
            if(null != listener){
                dialog.setOnDismissListener(listener)
            }
            return BaseDialog
        }
    }

    fun init(block: (v: View) -> Unit): BaseDialog {
        block(view!!)
        return this
    }

    fun setText(viewId: Int, text: String){
        val textView = view!!.findViewById<TextView>(viewId)
        textView.text = text
    }

    fun setProgressBar(viewId: Int, pro: Int){
        val progressBar = view!!.findViewById<ProgressBar>(viewId)
        progressBar.progress = pro
    }
}