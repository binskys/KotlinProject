package com.ruiec.testkotlin.utils

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.ruiec.testkotlin.R

/**
 *
 * @author pfm
 * @date 2021/3/11 10:38
 */
object NotificationUtil {
    /**
     * 判断是否需要打开设置界面
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun openNotificationSetting(
        context: Context,
        onNextListener: OnNextListener?
    ) {
        if (!isNotificationEnabled(context)) {
            showSettingDialog(context)
        } else {
            onNextListener?.onNext()
        }
    }

    //判断该app是否打开了通知
    /**
     * 可以通过NotificationManagerCompat 中的 areNotificationsEnabled()来判断是否开启通知权限。NotificationManagerCompat 在 android.support.v4.app包中，是API 22.1.0 中加入的。而 areNotificationsEnabled()则是在 API 24.1.0之后加入的。
     * areNotificationsEnabled 只对 API 19 及以上版本有效，低于API 19 会一直返回true
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun isNotificationEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val notificationManagerCompat =
                NotificationManagerCompat.from(context)
            return notificationManagerCompat.areNotificationsEnabled()
        }
        val checkOpNoThrow = "checkOpNoThrow"
        val opPostNotification = "OP_POST_NOTIFICATION"
        val mAppOps =
            context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val appInfo = context.applicationInfo
        val pkg = context.applicationContext.packageName
        val uid = appInfo.uid
        val appOpsClass: Class<*>?
        /* Context.APP_OPS_MANAGER */try {
            appOpsClass = Class.forName(AppOpsManager::class.java.name)
            val checkOpNoThrowMethod = appOpsClass.getMethod(
                checkOpNoThrow, Integer.TYPE, Integer.TYPE,
                String::class.java
            )
            val opPostNotificationValue =
                appOpsClass.getDeclaredField(opPostNotification)
            val value = opPostNotificationValue[Int::class.java] as Int
            return checkOpNoThrowMethod.invoke(
                mAppOps,
                value,
                uid,
                pkg
            ) as Int == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun showSettingDialog(mContext: Context) {
        //提示弹窗
        val dialog = MaterialDialog(mContext)
            .title(R.string.str_qxtx)
            .positiveButton(R.string.str_confirm, click = {
                goToSet(mContext)
            }).negativeButton(R.string.str_cancel)
        dialog.show()
    }

    //打开手机设置页面
    /**
     * 假设没有开启通知权限，点击之后就需要跳转到 APP的通知设置界面，对应的Action是：Settings.ACTION_APP_NOTIFICATION_SETTINGS, 这个Action是 API 26 后增加的
     * 如果在部分手机中无法精确的跳转到 APP对应的通知设置界面，那么我们就考虑直接跳转到 APP信息界面，对应的Action是：Settings.ACTION_APPLICATION_DETAILS_SETTINGS
     */
    private fun goToSet(context: Context) {
        val intent = Intent()
        when {
            Build.VERSION.SDK_INT >= 26 -> {
                // android 8.0引导
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
            }
            Build.VERSION.SDK_INT >= 21 -> {
                // android 5.0-7.0
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", context.packageName)
                intent.putExtra("app_uid", context.applicationInfo.uid)
            }
            else -> {
                // 其他
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", context.packageName, null)
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    interface OnNextListener {
        /**
         * 不需要设置通知的下一步
         */
        fun onNext()
    }
}