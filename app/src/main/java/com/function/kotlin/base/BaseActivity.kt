package com.function.kotlin.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.function.kotlin.mvp.IBaseView
import com.function.kotlin.mvp.presenter.IPresenter

/**
 * @author Zero degree
 * @date 2021/2/3 10:47
 * @功能:
 */

class BaseActivity<v:IBaseView,P:IPresenter<v>> :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}