package com.ruiec.testkotlin.mvp

import android.content.Context
import com.blankj.utilcode.util.AppUtils
import com.hjq.toast.ToastUtils
import com.ruiec.common_library.mvp.IModel
import com.ruiec.common_library.net.ApiErrorModel
import com.ruiec.common_library.net.ReqCallBack
import com.ruiec.testkotlin.R
import com.ruiec.testkotlin.net.HttpBuilder
import com.ruiec.testkotlin.update.UpdateDialog
import com.ruiec.testkotlin.update.VersionBean
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity


/**
 *
 * @author pfm
 * @date 2021/1/28 15:22
 */
open class BaseModel : IModel() {

    open fun getAppVersion(activity : RxAppCompatActivity, isToast: Boolean){
        req(activity, HttpBuilder.instance.getService().getAppVersion(), object :
            ReqCallBack<VersionBean> {
            override fun success(data: VersionBean) {
                val edition: Int = data.edition
                if (edition > AppUtils.getAppVersionCode()) {
                    //说明有新版本了，弹框更新对话框。
                    showUpdatesDialog(activity, data)
                } else {
                    //已经是最新版本了。
                    if (isToast) {
                        ToastUtils.show(activity.getString(R.string.str_current_new))
                    }
                }
            }

            override fun failure(statusCode: Int, errorModel: ApiErrorModel) {
                ToastUtils.show(errorModel.msg)
            }
        })
    }

    private lateinit var updateDialog : UpdateDialog
    private fun showUpdatesDialog(context: Context, bean: VersionBean){
        if(!this::updateDialog.isInitialized){
            updateDialog = UpdateDialog(context, bean)
            updateDialog.initDialog()
        }
        updateDialog.show()
    }
}