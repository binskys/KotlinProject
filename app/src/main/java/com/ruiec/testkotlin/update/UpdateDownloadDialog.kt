package com.ruiec.testkotlin.update

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.view.Window
import com.ayvytr.ktx.ui.onClick
import com.blankj.utilcode.util.ScreenUtils
import com.ruiec.testkotlin.R
import com.ruiec.testkotlin.constant.EventConstant
import com.ruiec.common_library.bean.EventMsgBean
import kotlinx.android.synthetic.main.dialog_update_download.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DecimalFormat

/**
 * 版本更新下载弹窗
 * @author pfm
 * @date 2021/3/11 14:12
 */
class UpdateDownloadDialog(context: Context) : Dialog(context) {

    init {
        val view = View.inflate(context, R.layout.dialog_update_download, null)
        this.setContentView(view)
        this.setCanceledOnTouchOutside(true)
        val dialogWindow: Window? = this.window
        val lp = dialogWindow!!.attributes
        lp.width = (ScreenUtils.getAppScreenWidth() * 0.8).toInt()
        dialogWindow.attributes = lp
        dialogWindow.setGravity(Gravity.CENTER)
        dialogWindow.setBackgroundDrawableResource(R.drawable.dialog_tra_shape)
        this.setOnDismissListener {
            EventBus.getDefault().unregister(this)
        }
    }

    fun initDialog(){
        tv_download_again.onClick {
            //继续下载
            tv_size.visibility = View.VISIBLE
            tv_download_again.visibility = View.GONE

            val b = Intent()
            b.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            b.action = "com.receiver.update.continue"
            context.sendBroadcast(b)
        }
    }

    fun showDialog() {
        if (!this.isShowing) {
            show()
        }
        EventBus.getDefault().register(this)
    }

    private fun dismissDialog() {
        EventBus.getDefault().unregister(this)
        dismiss()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMsgEvent(event: EventMsgBean) {
        when (event.type) {
            EventConstant.UPDATE_APP_DEFAULT_TOAST -> {
                //更新失败
                tv_size.visibility = View.GONE
                tv_download_again.visibility = View.VISIBLE
            }
            EventConstant.UPDATE_APP_PROGRESS -> {
                //更新进度
                tv_size.visibility = View.VISIBLE
                tv_download_again.visibility = View.GONE
                val bean = event.bean as AppDownloadBean
                seek_bar.progress = bean.progress
                seek_bar_bg.progress = bean.progress
                val str = bean.progress.toString() + "%"
                tv_progress.text = str
                val df = DecimalFormat("######0.00")
                val size = df.format(bean.downloadSize).toString() + "M/" + bean.countSize + "M"
                tv_size.text = size
            }
            EventConstant.DOWN_LOAD_FILE_SUCCESS -> {
                //更新成功
                dismissDialog()
            }
        }
    }
}