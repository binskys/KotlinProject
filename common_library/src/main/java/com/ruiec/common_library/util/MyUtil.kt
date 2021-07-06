package com.ruiec.common_library.util

import android.content.Context
import android.text.TextUtils
import com.blankj.utilcode.util.AppUtils
import java.io.File

/**
 *
 * @author pfm
 * @date 2021/3/16 10:01
 */
object MyUtil {

    /**
     * 设置鲁班压缩目录
     *
     * @return
     */
    fun getLuBanDir(context: Context): String? {
        val dir: String = context.getExternalFilesDir(null)?.absolutePath + "/"+ AppUtils.getAppPackageName() + "/image/"
        val file = File(dir)
        if (!file.exists()) {
            file.mkdirs()
        }
        return dir
    }

    /**
     * 获取文件名及后缀
     */
    fun getFileNameWithSuffix(path: String): String? {
        if (TextUtils.isEmpty(path)) {
            return ""
        }
        val start = path.lastIndexOf("/")
        return if (start != -1) {
            path.substring(start + 1)
        } else {
            ""
        }
    }
}