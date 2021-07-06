package com.ruiec.common_library.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ruiec.common_library.bean.EventMsgBean
import com.ruiec.common_library.mvp.BasePresenter
import com.ruiec.common_library.mvp.BaseView
import com.ruiec.common_library.util.LoadingDialog
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Fragment基类
 * @author pfm
 * @date 2021/2/22 17:24
 */
abstract class BaseFragment<P : BasePresenter> : BasePermissionsFragment(), BaseView {

    lateinit var mRootView : View
    lateinit var presenter: P

    var isFirstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(bindLayoutId(), null)
        init()
        loadNetData()
        return mRootView
    }

    abstract fun bindLayoutId() : Int

    abstract fun onCreate()

    abstract fun bindPresenter() : P

    private fun init(){
        //注册事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        presenter = bindPresenter()
    }

    fun setLinearLayoutManager(recyclerView: RecyclerView, isPortrait : Boolean){
        val layoutManager = LinearLayoutManager(activity)
        if(!isPortrait){
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        }
        recyclerView.layoutManager = layoutManager
    }

    fun setRefreshStyleAndListener(smartRefreshLayout: SmartRefreshLayout){
        smartRefreshLayout.setRefreshHeader(ClassicsHeader(activity))
        smartRefreshLayout.setRefreshFooter(ClassicsFooter(activity))
        smartRefreshLayout.setOnRefreshListener{
            refreshData()
            smartRefreshLayout.finishRefresh(2000/*,false*/)//传入false表示刷新失败
        }
        smartRefreshLayout.setOnLoadMoreListener {
            loadMoreData()
            smartRefreshLayout.finishLoadMore(2000/*,false*/)//传入false表示加载失败
        }
    }

    //刷新
    open fun refreshData(){

    }

    //加载更多
    open fun loadMoreData(){

    }

    open fun loadNetData(){

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(messageEvent : EventMsgBean){
        //广播接收eventBus
    }

    override fun showLoading() {
        LoadingDialog.show(activity!!)
    }

    override fun dismissLoading() {
        LoadingDialog.cancel()
    }

    override fun onResume() {
        super.onResume()
        if(isFirstLoad){
            isFirstLoad = false
            onCreate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}