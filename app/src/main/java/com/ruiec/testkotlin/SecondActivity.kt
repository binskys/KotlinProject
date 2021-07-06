package com.ruiec.testkotlin

import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.ruiec.common_library.base.BaseActivity
import com.ruiec.common_library.base.BaseBean
import com.ruiec.common_library.mvp.BasePresenter
import com.ruiec.testkotlin.net.HttpBuilder
import com.ruiec.testkotlin.update.VersionBean
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.coroutines.*
import java.util.ArrayList

/**
 *
 * @author pfm
 * @date 2021/2/2 17:25
 */
open class SecondActivity : BaseActivity<BasePresenter>(), View.OnClickListener {

    override fun bindLayout(): Int {
        return R.layout.activity_second
    }

    override fun onCreate() {
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.transparent))
    }

    override fun bindPresenter(): BasePresenter {
        return BasePresenter(this)
    }

    private fun initDialogView(view: View){
        view.findViewById<TextView>(R.id.tv_question1).setOnClickListener {
            ToastUtils.showShort("111")
        }
    }

    private fun showDialog(){
        LogUtils.d("点击了EventBus")
        val v = View.inflate(this, R.layout.dialog_bottom_sheet, null)
        initDialogView(v)
        val dialog = MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT))
            .customView(null, v)
        dialog.show {
            cornerRadius(15f)
        }
    }

    fun load(){
        GlobalScope.launch(Dispatchers.Main) {
            var name = ""
//                    withContext(Dispatchers.IO){
//                        val result = HttpBuilder.instance.getService().getAppVersion2().data
//                        name = result.abstractX
//                    }

            val result1 = async {
                HttpBuilder.instance.getService().getAppVersion2().data.title
            }
            val result2 = async {
                HttpBuilder.instance.getService().getAppVersion2().data.abstractX
            }
            name = result1.await() + result2.await()
            btnSendEvent.text = name
        }
    }

    fun load2(){
        retrofit<VersionBean> {
            api = HttpBuilder.instance.getService().getAppVersion3()
            onSuccess {
                btnSendEvent.text = it.abstractX
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnSendEvent ->
                //showDialog()
                load2()
            R.id.btn2 ->{
                val intent = Intent(this, TestFragmentActivity::class.java)
                startActivity(intent)
            }
        }
    }
}