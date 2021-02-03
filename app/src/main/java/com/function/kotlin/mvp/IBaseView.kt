package com.function.kotlin.mvp

import android.content.Context
import android.widget.Toast
import com.function.kotlin.mvp.presenter.IPresenter

/**
 * @author Zero degree
 * @date 2021/2/3 9:28
 * @功能:
 */

interface IBaseView{

    fun initView()

    fun showLoading(){

    }

    fun hideLoading(){

    }

    fun showToast(context: Context, text:String){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show()
    }
}
