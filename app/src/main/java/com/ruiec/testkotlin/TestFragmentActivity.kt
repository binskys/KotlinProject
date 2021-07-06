package com.ruiec.testkotlin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.blankj.utilcode.util.LogUtils
import com.ruiec.common_library.base.BaseActivity
import com.ruiec.common_library.mvp.BasePresenter
import com.ruiec.testkotlin.fragment.TestFragment
import kotlinx.android.synthetic.main.activity_third.*
import java.util.ArrayList

/**
 *
 * @author pfm
 * @date 2021/3/8 17:06
 */
open class TestFragmentActivity : BaseActivity<BasePresenter>(){
    var fragmentList = ArrayList<Fragment>()

    override fun bindLayout(): Int {
        return R.layout.activity_third
    }

    override fun onCreate() {
        headView.setTitle("FragmentActivity")

        fragmentList.clear()
        for (index in 1..3){
            LogUtils.d("index:$index")
            fragmentList.add(TestFragment())
        }
        viewPager.adapter = object :
            FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return fragmentList[position]
            }

            override fun getCount(): Int {
                return fragmentList.size
            }
        }
    }

    override fun bindPresenter(): BasePresenter {
        return BasePresenter(this)
    }
}