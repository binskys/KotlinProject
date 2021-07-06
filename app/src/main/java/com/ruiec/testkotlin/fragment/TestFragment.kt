package com.ruiec.testkotlin.fragment

import com.blankj.utilcode.util.ToastUtils
import com.ruiec.testkotlin.R
import com.ruiec.common_library.base.BaseFragment
import com.ruiec.common_library.mvp.BasePresenter
import kotlinx.android.synthetic.main.fragment_test.*

/**
 *
 * @author pfm
 * @date 2021/3/8 16:45
 */
open class TestFragment : BaseFragment<BasePresenter>(){
    override fun bindLayoutId(): Int {
        return R.layout.fragment_test
    }

    override fun onCreate() {
        tvTip.text = "看见时状态改变"
        ToastUtils.showShort(tvTip.text.toString())
    }

    override fun bindPresenter(): BasePresenter {
        return BasePresenter(this)
    }
}