package com.ruiec.testkotlin

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.ruiec.common_library.adapter.CommonBannerAdapter
import com.ruiec.common_library.base.BaseActivity
import com.ruiec.testkotlin.bean.BannerVipBean
import com.ruiec.testkotlin.bean.HomeBean
import com.ruiec.testkotlin.constant.EventConstant
import com.ruiec.testkotlin.mvp.home.HomePresenter
import com.ruiec.testkotlin.mvp.home.HomeView
import com.ruiec.common_library.net.upload.OkHttpUploadUtil
import com.ruiec.common_library.net.upload.UploadFileBean
import com.ruiec.testkotlin.update.UpdateService
import com.ruiec.common_library.bean.EventMsgBean
import com.ruiec.common_library.widget.adapter.RVAdapter
import com.ruiec.common_library.widget.adapter.RViewHolder
import com.ruiec.common_library.widget.adapter.RvEmptyAdapter
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import kotlin.collections.ArrayList
import kotlin.collections.MutableList


open class MainActivity : BaseActivity<HomePresenter>(), HomeView {
    private lateinit var homeAdapter: RVAdapter<HomeBean.MatchBean>
    private var matchList : MutableList<HomeBean.MatchBean> = ArrayList()

    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun bindPresenter(): HomePresenter {
        return HomePresenter(this, this)
    }


    override fun onCreate() {
        //禁止侧滑返回
        setSwipeBackEnable(false)

        headView.setTitle("Hello world!")
        setGridLayoutManager(emptyRecyclerView, 2)
        emptyRecyclerView.isNestedScrollingEnabled = false
        setRefreshStyleAndListener(smartRefreshLayout)
    }

    override fun loadNetData() {
        super.loadNetData()
        presenter.getHomeData()
        presenter.getBannerVip(1, 1)
    }

    open fun takePhoto(view : View){
        //requestLocationWithPermissionCheck()
        checkPhoto(0, true)
        //presenter.checkUpdate(this, true)

//        OkHttpDownloadUtil.download(this, "abc.apk", "https://betapc.cchccc.com/storage/file/201223/1608711159.apk", object : OkHttpDownloadUtil.MyCallBack {
//            override fun onSuccess(path: String) {
//                //下载成功
//                com.hjq.toast.ToastUtils.show(getString(R.string.str_xzsb))
//            }
//
//            override fun onFailure() {
//                //下载失败
//            }
//        })
    }

    override fun onResultImgPathAfterCrop(imgPath: String) {
        super.onResultImgPathAfterCrop(imgPath)
        val  file = File(imgPath)
        if(file.exists()){
            OkHttpUploadUtil.uploadFile(this, file, "6", object : OkHttpUploadUtil.UploadListener {
                override fun onSuccess(bean: UploadFileBean) {
                    ToastUtils.showShort(getString(R.string.str_sccg))
                }

                override fun onFailure() {
                }

            })
        }
    }

    override fun openLocation() {
        super.openLocation()
        Toast.makeText(this, "您已开启了定位权限", Toast.LENGTH_SHORT).show()
    }

    override fun setHomeData(bean: HomeBean) {
        matchList.clear()
        matchList.addAll(bean.match)
        setAdapter()
    }

    override fun setBannerData(bean: BannerVipBean) {
        val adapter = object : CommonBannerAdapter<BannerVipBean.BannerBean>(this, bean.banner){
            override fun bindView(view: ImageView, data: BannerVipBean.BannerBean, position: Int) {
                val imageUrl: String = "https://betapc.cchccc.com" + data.img
                Glide.with(this@MainActivity)
                    .load(imageUrl)
                    .into(view)
            }
        }

        homeBanner.let {
            it.adapter = adapter
            it.addBannerLifecycleObserver(this)
            it.setIndicator(homeIndicatorView, false)
            it.setOnBannerListener(OnBannerListener<BannerVipBean.BannerBean> { data: BannerVipBean.BannerBean, _ ->
                ToastUtils.showShort(data.img)
            })
        }
    }

    private fun setAdapter(){
        if(this::homeAdapter.isInitialized){
            homeAdapter.notifyDataSetChanged()
        } else{
            homeAdapter = object :
                RVAdapter<HomeBean.MatchBean>(this@MainActivity, R.layout.item_home_match, matchList){
                override fun convert(holder: RViewHolder, itemBean: HomeBean.MatchBean, position: Int) {
                    val ivHead  = holder.getView<ImageView>(R.id.item_home_recycler1_ivHead)
                    val tvTitle2 = holder.getView<TextView>(R.id.item_tvTitle2)
                    val tvTime2 = holder.getView<TextView>(R.id.item_tvTime2)
                    val tvTitle = holder.getView<TextView>(R.id.item_tvTitle)
                    val tvRegister = holder.getView<TextView>(R.id.item_tvTime)
                    val llType = holder.getView<LinearLayout>(R.id.ll_type)
                    val tvWatch = holder.getView<TextView>(R.id.item_watch)

                    tvTitle.text = itemBean.title
                    tvTitle2.text = itemBean.title
                    //时间
                    val time = itemBean.start_time + "-" + itemBean.end_time
                    tvTime2.text = time
                    //报名人数
                    val register = "已报名：" + itemBean.sign_up_num
                    tvRegister.text = register

                    Glide.with(this@MainActivity).load("https://betapc.cchccc.com" + itemBean.logo).into(ivHead)

                    if (itemBean.banner_type == 1){
                        llType.visibility = View.VISIBLE
                    }else {
                        llType.visibility = View.GONE
                    }
                    tvWatch.text = itemBean.click.toString()
                }
            }
            homeAdapter.setOnItemClickListener(object : RvEmptyAdapter.OnItemClickListener{
                override fun onItemClick(
                    view: View?,
                    holder: RecyclerView.ViewHolder?,
                    position: Int
                ) {
                    val intent = Intent(this@MainActivity, SecondActivity::class.java)
                    startActivity(intent)
                }
            })
            emptyRecyclerView.adapter = homeAdapter

            setEnableLoadMore<HomeBean.MatchBean>(smartRefreshLayout, emptyRecyclerView, false)
        }
    }

    override fun onEvent(messageEvent: EventMsgBean) {
        super.onEvent(messageEvent)
        if(messageEvent.type == EventConstant.HOME_REFRESH){
            text_view.text = messageEvent.msg
        } else if (messageEvent.type == EventConstant.DOWN_LOAD_FILE_SUCCESS) {
            //下载新版APK成功 关闭服务
            val intent = Intent(this@MainActivity, UpdateService::class.java)
            stopService(intent)
        }
    }
}
