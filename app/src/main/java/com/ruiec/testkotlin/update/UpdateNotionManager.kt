package com.ruiec.testkotlin.update

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Process
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.AppUtils
import com.ruiec.testkotlin.R
import java.io.File

/**
 *
 * @author pfm
 * @date 2021/3/11 11:20
 */
object UpdateNotionManager {

    private var mNotificationChannel: NotificationChannel? = null
    private var mNotificationManager: NotificationManager? = null
    private var context: Context? = null
    private var mNotification: Notification? = null

    /** 下载的APK文件  */
    private var apkFile: File? = null
    private const val notificationTag = "notification_tag"

    /** 自定义视图  */
    private var mRemoteViews: RemoteViews? = null

    /**
     * 更新通知栏
     *
     * @param apkFile 文件
     * @param progress 进度
     * @param downLoadType 下载的类型 0：下载中  1：下载暂停 2：下载失败
     */
    fun notifyUser(
        context: Context,
        progress: Int,
        apkFile: File?,
        downLoadType: Int
    ) {
        this.context = context
        this.apkFile = apkFile
        if (mNotificationManager == null) {
            mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "2021"
            val channelName = "Channel1"
            if (mNotificationChannel == null) {
                //创建 通知通道  channelId和channelName是必须的（自己命名就好）
                mNotificationChannel =
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                //是否在桌面icon右上角展示小红点
                mNotificationChannel!!.enableLights(true)
                //小红点颜色
                mNotificationChannel!!.lightColor = Color.GREEN
                //是否在久按桌面图标时显示此渠道的通知
                mNotificationChannel!!.setShowBadge(true)
                mNotificationManager!!.createNotificationChannel(mNotificationChannel!!)
            }
            val builder =
                NotificationCompat.Builder(
                    context.applicationContext,
                    channelId
                )
            builder.setOnlyAlertOnce(true)
            //自定义通知栏
            mRemoteViews = RemoteViews(AppUtils.getAppPackageName(), R.layout.update_notification)
            setRemoteViewsOnClickListener(mRemoteViews)
            builder.setSmallIcon(R.mipmap.ic_launcher) //设置自定义通知栏的视图
                .setContent(mRemoteViews) //设置通知栏移除的监听事件
                .setDeleteIntent(getCancelIntent())
            //设置进度
            if (downLoadType == 0) {
                //下载中不能移除通知栏
                builder.setOngoing(true)
                mRemoteViews!!.setViewVisibility(R.id.tv_continue, View.GONE)
                mRemoteViews!!.setViewVisibility(R.id.tv_stop, View.GONE)
                if (progress in 1..100) {
                    val progressTitle =
                        context.getString(R.string.str_download_pro) + progress + "%"
                    mRemoteViews!!.setTextViewText(R.id.tv_download_progress, progressTitle)
                    mRemoteViews!!.setProgressBar(R.id.progress, 100, progress, false)
                    if (progress == 100) {
                        mRemoteViews!!.setTextViewText(
                            R.id.tv_download_progress,
                            context.getString(R.string.str_download_finish)
                        )
                    }
                } else {
                    mRemoteViews!!.setProgressBar(R.id.progress, 100, 0, false)
                }
            } else {
                mRemoteViews!!.setViewVisibility(R.id.tv_continue, View.GONE)
                mRemoteViews!!.setViewVisibility(R.id.tv_stop, View.GONE)
                mRemoteViews!!.setProgressBar(R.id.progress, 100, progress, false)
                if (downLoadType == 1) {
                    mRemoteViews!!.setTextViewText(
                        R.id.tv_download_progress,
                        context.getString(R.string.str_download_pause)
                    )
                } else if (downLoadType == 2) {
                    mRemoteViews!!.setTextViewText(
                        R.id.tv_download_progress,
                        context.getString(R.string.str_xzsb)
                    )
                    mRemoteViews!!.setViewVisibility(R.id.tv_continue, View.VISIBLE)
                }
            }
            val notificationId = 0x1234
            mNotificationManager!!.notify(notificationTag, notificationId, builder.build())
        } else {
            //7.0,7.1以下版本
            val builder =
                NotificationCompat.Builder(context)
            builder.setOnlyAlertOnce(true)
            // 7.0以上的手机通知栏会出现小白框图标。为了处理该问题，图标设置为没有背景色的图标即可解决。
            // builder.setSmallIcon(xxx);  xxx: 要设置为透明背景色的图标
            mRemoteViews = RemoteViews(AppUtils.getAppPackageName(), R.layout.update_notification)
            setRemoteViewsOnClickListener(mRemoteViews)
            builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContent(mRemoteViews)
                .setDeleteIntent(getCancelIntent())
            if (downLoadType == 0) {
                //下载中设置左右滑动不能删除
                builder.setOngoing(true)
                //显示暂停按钮
                mRemoteViews!!.setViewVisibility(R.id.tv_continue, View.GONE)
                mRemoteViews!!.setViewVisibility(R.id.tv_stop, View.VISIBLE)
                mRemoteViews!!.setTextViewText(
                    R.id.tv_download_progress,
                    context.getString(R.string.str_zzxz)
                )
                if (progress in 1..100) {
                    val progressTitle =
                        context.getString(R.string.str_download_pro) + progress + "%"
                    mRemoteViews!!.setTextViewText(R.id.tv_download_progress, progressTitle)
                    mRemoteViews!!.setProgressBar(R.id.progress, 100, progress, false)
                    if (progress == 100) {
                        mRemoteViews!!.setTextViewText(
                            R.id.tv_download_progress,
                            context.getString(R.string.str_download_finish)
                        )
                    }
                } else {
                    mRemoteViews!!.setProgressBar(R.id.progress, 100, 0, false)
                }
            } else {
                mRemoteViews!!.setViewVisibility(R.id.tv_continue, View.GONE)
                mRemoteViews!!.setViewVisibility(R.id.tv_stop, View.GONE)
                mRemoteViews!!.setProgressBar(R.id.progress, 100, progress, false)
                if (downLoadType == 1) {
                    mRemoteViews!!.setTextViewText(
                        R.id.tv_download_progress,
                        context.getString(R.string.str_download_pause)
                    )
                } else if (downLoadType == 2) {
                    mRemoteViews!!.setTextViewText(
                        R.id.tv_download_progress,
                        context.getString(R.string.str_xzsb)
                    )
                    mRemoteViews!!.setViewVisibility(R.id.tv_continue, View.VISIBLE)
                }
            }
            builder.setWhen(System.currentTimeMillis())
            mNotification = builder.build()
            mNotificationManager!!.notify(notificationTag, 2, mNotification)
        }
    }

    private fun setRemoteViewsOnClickListener(remoteViews: RemoteViews?) {
        //设置暂停按钮的点击事件
        val a = Intent()
        a.action = "com.receiver.update.stop"
        //发送广播到UpdateService服务去暂停下载
        remoteViews!!.setOnClickPendingIntent(
            R.id.tv_stop, PendingIntent.getBroadcast(
                context,
                10, a, PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        //设置继续下载按钮的点击事件
        val b = Intent()
        b.action = "com.receiver.update.continue"
        //发送广播到UpdateService服务去继续下载
        remoteViews.setOnClickPendingIntent(
            R.id.tv_continue, PendingIntent.getBroadcast(
                context,
                11, b, PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    /** 通知栏移除事件  */
    private fun getCancelIntent(): PendingIntent? {
        val cancelIntent = Intent()
        cancelIntent.action = "com.receiver.update.cancel"
        //发送广播去停止下载服务
        return PendingIntent.getBroadcast(
            context,
            12,
            cancelIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    /**
     * 进入安装
     *
     * @return
     */
    private fun getContentIntent(): PendingIntent? {
        mNotificationManager!!.cancelAll()
        //移除通知栏
        if (Build.VERSION.SDK_INT >= 26) {
            mNotificationManager!!.deleteNotificationChannel("1")
        }
        /*** 7.0,8.0兼容 * targetSdkVersion 26 */
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= 26) {
            //8.0(26)，8.1(27)
            val apkUri = FileProvider.getUriForFile(
                context!!.applicationContext,
                context!!.packageName + ".fileprovider",
                apkFile!!
            )
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else if (Build.VERSION.SDK_INT >= 23) {
            //6.0(23)；7.0(24)；7.1(25)；
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(
                Uri.fromFile(apkFile),
                "application/vnd.android.package-archive"
            )
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setDataAndType(
                Uri.fromFile(apkFile),
                "application/vnd.android.package-archive"
            )
        }
        context!!.startActivity(intent)
        Process.killProcess(Process.myPid())
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        context!!.startActivity(intent)
        return pendingIntent
    }

    fun remove() {
        if (mNotificationManager == null) {
            return
        }
        mNotificationManager!!.cancelAll()
        //移除通知栏
        if (Build.VERSION.SDK_INT >= 26) {
            mNotificationManager!!.cancel(notificationTag, 0x1234)
            //mNotificationManager.deleteNotificationChannel("2018");
        } else {
            mNotificationManager!!.cancel(notificationTag, 2)
        }
    }
}