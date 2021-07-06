package com.ruiec.common_library.base

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.UriUtils
import com.ruiec.common_library.Constant.RequestIntentConstant
import com.ruiec.common_library.R
import com.ruiec.common_library.bean.EventMsgBean
import com.ruiec.common_library.mvp.BasePresenter
import com.ruiec.common_library.mvp.BaseView
import com.ruiec.common_library.net.RetrofitCoroutineDSL
import com.ruiec.common_library.util.LoadingDialog
import com.ruiec.common_library.util.MyUtil
import com.ruiec.common_library.util.PhotoUtils
import com.ruiec.common_library.widget.GridDividerItemDecoration
import com.ruiec.common_library.widget.adapter.RVAdapter
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.IOException
import java.net.ConnectException
import kotlin.coroutines.CoroutineContext

/**
 * 基类Activity
 * @author pfm
 * @date 2021/1/27 11:49
 */
abstract class BaseActivity<P : BasePresenter> : BasePermissionsActivity(), BaseView,
    CoroutineScope{
    lateinit var presenter: P

    /** 限制文件类型  0：不限制 */
    private var limitFileType = 0
    /** 选择拍照的图片路径 */
    private var file: File? = null
    /** 文件资源标识 */
    private lateinit var uri: Uri
    /** 是否需要裁减 */
    private var needCrop = false

    /*** 0:是用户头像（正方形、圆形）；1、banner （长方形） */
    private var cropType = 0
    /** 裁减比例 */
    private val cropAspectX = 1
    private val cropAspectY = 1

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        setContentView(bindLayout())
        init()
        onCreate()
        loadNetData()
    }


    private fun init(){
        //禁止横竖屏切换
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //初始化软键盘弹出方式
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        //初始化状态栏颜色
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_ff))
        BarUtils.setStatusBarLightMode(this, true)

        presenter = bindPresenter()

        EventBus.getDefault().register(this)
    }

    abstract fun onCreate()

    abstract fun bindLayout():Int

    abstract fun bindPresenter() : P

    override fun showLoading() {
        LoadingDialog.show(this)
    }

    override fun dismissLoading() {
        LoadingDialog.cancel()
    }

    fun setLinearLayoutManager(recyclerView: RecyclerView, isPortrait : Boolean){
        val layoutManager = LinearLayoutManager(this)
        if(!isPortrait){
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        }
        recyclerView.layoutManager = layoutManager
    }

    fun setGridLayoutManager(recyclerView: RecyclerView, column: Int){
        setGridLayoutManager(recyclerView, column, SizeUtils.dp2px(10f), Color.TRANSPARENT)
    }

    open fun setGridLayoutManager(recyclerView: RecyclerView, column: Int, width : Int, @ColorInt color: Int){
        recyclerView.layoutManager = GridLayoutManager(this, column)
        val itemDecoration = GridDividerItemDecoration(width, color)
        recyclerView.addItemDecoration(itemDecoration)
    }

    /**
     * 是否是否可以加载更多
     *
     * @param isLoadMore false: 加载更多不可用了。
     */
    @Suppress("UNCHECKED_CAST")
    open fun <T>setEnableLoadMore(mSwipeRefresh: SmartRefreshLayout, baseRecycler: RecyclerView, isLoadMore: Boolean) {
        mSwipeRefresh.setEnableLoadMore(isLoadMore)
        if (!isLoadMore) {
            val adapter: RVAdapter<T> = baseRecycler.adapter as RVAdapter<T>
            val footer: View
            if (null != adapter.getHeaderView() && adapter.itemCount >= 2
                || null == adapter.getHeaderView() && adapter.itemCount >= 1
            ) {
                footer = LayoutInflater.from(this)
                    .inflate(R.layout.recycler_footer_view, baseRecycler, false)
                adapter.setFooterView(footer)
            }
        } else {
            val adapter: RVAdapter<T> =
                baseRecycler.adapter as RVAdapter<T>
            adapter.setFooterView(null)
        }
    }

    fun setRefreshStyleAndListener(smartRefreshLayout: SmartRefreshLayout){
        smartRefreshLayout.setRefreshHeader(ClassicsHeader(this))
        smartRefreshLayout.setRefreshFooter(ClassicsFooter(this))
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

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun <T> CoroutineScope.retrofit(dsl: RetrofitCoroutineDSL<T>.() -> Unit){
        this.launch(Dispatchers.Main) {
            val coroutine = RetrofitCoroutineDSL<T>().apply(dsl)
            coroutine.api?.let { call ->
                val deferred = async(Dispatchers.IO) {
                    try {
                        call.execute() //已经在io线程中了，所以调用Retrofit的同步方法
                    } catch (e: ConnectException) {
                        coroutine.onFail?.invoke("网络连接出错", -1)
                        null
                    } catch (e: IOException) {
                        coroutine.onFail?.invoke("未知网络错误", -1)
                        null
                    }
                }
                //当协程取消的时候，取消网络请求
                deferred.invokeOnCompletion {
                    if(deferred.isCancelled){
                        call.cancel()
                        coroutine.clean()
                    }
                }
                //等待异步的结果
                val result = deferred.await()
                if(null == result){
                    coroutine.onFail?.invoke("返回为空", -1)
                } else{
                    result.let {
                        if(it.isSuccessful){
                            //接口访问成功
                            if(result.body()?.status == 1 || result.body()?.status == 200){
                                coroutine.onSuccess?.invoke(result.body()!!.data)
                            } else{
                                coroutine.onFail?.invoke(result.body()!!.msg, result.body()!!.code)
                            }
                        } else{
                            coroutine.onFail?.invoke(result.errorBody().toString(), result.code())
                        }
                    }
                }
                coroutine.onComplete?.invoke()
            }

        }
    }

    /** 打开系统的文件选择器  */
    open fun pickFiles() {
        limitFileType = 0
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        this.startActivityForResult(intent, RequestIntentConstant.CHOOSE_FILE_REQUEST)
    }

    /**
     * 拍照或选择照片
     *
     * @param type 0：拍照 1：相册
     * @param needCrop true:需要裁减 false:不需要
     * */
    open fun checkPhoto(type: Int, needCrop: Boolean){
        this.needCrop = needCrop
        if(type == 0){
            takePhotoWithPermissionCheck()
        } else if(type == 1){
            selectPhotoWithPermissionCheck()
        }
    }

    override fun takePhoto() {
        super.takePhoto()
        //拍照选择
        this.uri = PhotoUtils.createUriForN(this)!!
        if (needCrop) {
            PhotoUtils.toCameraForNAndCrop(this, uri)
        } else {
            PhotoUtils.toCameraForN(this, uri)
        }
    }

    override fun selectPhoto() {
        super.selectPhoto()
        //相册选择
        if (needCrop) {
            PhotoUtils.toPhotoAlbumAndCrop(this)
        } else {
            PhotoUtils.toPhotoAlbum(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(messageEvent : EventMsgBean){
        //广播接收eventBus
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                RequestIntentConstant.TAKE_PHOTO_REQUEST ->{
                    //拍照
                    file = null
                    file = if(data != null && data.data != null){
                        UriUtils.uri2File(data.data)
                    } else{
                        UriUtils.uri2File(uri)
                    }
                    onCompressionImage(file)
                }
                RequestIntentConstant.CROP_CAMERA_REQUEST ->{
                    //拍照并裁减
                    if (cropType == 1) {
                        //banner裁剪
                        startActivityForResult(
                            PhotoUtils.cropPhotoBanner(this, uri, cropAspectX, cropAspectY),
                            RequestIntentConstant.CROP_RESULT_REQUEST
                        )
                    } else {
                        startActivityForResult(
                            PhotoUtils.cropPhoto(this, uri),
                            RequestIntentConstant.CROP_RESULT_REQUEST
                        )
                    }
                }
                RequestIntentConstant.CHOOSE_PHOTO_REQUEST ->{
                    //选择相册
                    file = null
                    if(data != null && data.data != null){
                        file = UriUtils.uri2File(data.data)
                    }
                    onCompressionImage(file)
                }
                RequestIntentConstant.CROP_PHOTO_REQUEST ->{
                    //选择相册并裁剪
                    if (data != null && data.data != null) {
                        if (cropType == 1) {
                            //banner裁剪
                            startActivityForResult(
                                PhotoUtils.cropPhotoBanner(this, data.data, cropAspectX, cropAspectY),
                                RequestIntentConstant.CROP_RESULT_REQUEST
                            )
                        } else {
                            startActivityForResult(
                                PhotoUtils.cropPhoto(this, data.data),
                                RequestIntentConstant.CROP_RESULT_REQUEST
                            )
                        }
                    }
                }
                RequestIntentConstant.CROP_RESULT_REQUEST ->{
                    //裁减后返回结果
                    if(null != data){
                        onResultImgPathAfterCrop(PhotoUtils.cropPath)
                    }
                }
                RequestIntentConstant.CHOOSE_FILE_REQUEST ->{
                    //选择文件
                    if (data == null || data.data == null) {
                        // 用户未选择任何文件，直接返回
                        return
                    }
                    file = UriUtils.uri2File(data.data)
                    onResultFilePath(file)
                }
            }
        }
    }

    private fun onCompressionImage(file: File?){
        Luban.with(this).load(file)
            .ignoreBy(100).setTargetDir(MyUtil.getLuBanDir(this))
            .setCompressListener(object : OnCompressListener {
                override fun onSuccess(f: File?) {
                    //压缩成功
                    this@BaseActivity.file = f
                    onResultImgPath(f)
                }

                override fun onError(e: Throwable?) {
                    //压缩失败
                }

                override fun onStart() {
                    //开始压缩
                }
            })
    }

    /**
     * 裁剪后返回图片路径
     *
     * @param imgPath String 图片路径
     */
    open fun onResultImgPathAfterCrop(imgPath: String) {}

    /**
     * 选择文件或拍照的File
     *
     * @param file 文件
     * */
    open fun onResultImgPath(file: File?){}

    /**
     * 返回选择的文件
     *
     * @param file 文件
     */
    open fun onResultFilePath(file: File?) {}

    override fun onDestroy() {
        presenter.detachView()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
        // 关闭页面后，结束所有协程任务
        job.cancel()
    }
}