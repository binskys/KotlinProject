package com.ruiec.testkotlin

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.hjq.toast.ToastUtils
import com.ruiec.testkotlin.net.HttpBuilder
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits

/**
 *
 * @author pfm
 * @date 2021/1/28 16:18
 */
class MyApplication : Application(){

    private lateinit var instance : MyApplication

    fun getInstance(): MyApplication{
        return instance
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //分包配置
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this.applicationContext as MyApplication

        //初始化Http配置
        HttpBuilder.instance.init()

        //今日头条屏幕适配
        configUnits()

        //吐司
        ToastUtils.init(this)
    }

    private fun configUnits() {
        //AndroidAutoSize 默认开启对 dp 的支持, 调用 UnitsManager.setSupportDP(false); 可以关闭对 dp 的支持
        //主单位 dp 和 副单位可以同时开启的原因是, 对于旧项目中已经使用了 dp 进行布局的页面的兼容
        //让开发者的旧项目可以渐进式的从 dp 切换到副单位, 即新页面用副单位进行布局, 然后抽时间逐渐的将旧页面的布局单位从 dp 改为副单位
        //最后将 dp 全部改为副单位后, 再使用 UnitsManager.setSupportDP(false); 将 dp 的支持关闭, 彻底隔离修改 density 所造成的不良影响
        //如果项目完全使用副单位, 则可以直接以像素为单位填写 AndroidManifest 中需要填写的设计图尺寸, 不需再把像素转化为 dp

        //AndroidAutoSize 默认开启对 sp 的支持, 调用 UnitsManager.setSupportSP(false); 可以关闭对 sp 的支持
        //如果关闭对 sp 的支持, 在布局时就应该使用副单位填写字体的尺寸
        //如果开启 sp, 对其他三方库控件影响不大, 也可以不关闭对 sp 的支持, 这里我就继续开启 sp, 请自行斟酌自己的项目是否需要关闭对 sp 的支持
//                .setSupportSP(false)

        //AndroidAutoSize 默认不支持副单位, 调用 UnitsManager#setSupportSubunits() 可选择一个自己心仪的副单位, 并开启对副单位的支持
        //只能在 pt、in、mm 这三个冷门单位中选择一个作为副单位, 三个单位的适配效果其实都是一样的, 您觉的哪个单位看起顺眼就用哪个
        //您选择什么单位就在 layout 文件中用什么单位进行布局, 我选择用 mm 为单位进行布局, 因为 mm 翻译为中文是妹妹的意思
        //如果大家生活中没有妹妹, 那我们就让项目中最不缺的就是妹妹!
        AutoSizeConfig.getInstance().unitsManager.setSupportDP(true).supportSubunits = Subunits.MM
    }
}