package com.ruiec.common_library.util

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import java.io.*

/**
 *
 * @author pfm
 * @date 2021/3/17 10:59
 */
object CopyUtil {

    fun copyFile(context: Context, sourceFilePath: String, insertUri: Uri?): Boolean {
        if(null == insertUri){
            LogUtils.e("Uri为空")
            return false
        }
        val resolver = context.contentResolver
        var `is`: InputStream? = null //输入流
        var os: OutputStream? = null //输出流
        return try {
            os = resolver.openOutputStream(insertUri)
            if (os == null) {
                return false
            }
            val sourceFile = File(sourceFilePath)
            if (sourceFile.exists()) { // 文件存在时
                `is` = FileInputStream(sourceFile) // 读入原文件
                //输入流读取文件，输出流写入指定目录
                return copyFileWithStream(
                    os,
                    `is`
                )
            }
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            try {
                `is`?.close()
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun copyFileWithStream(
        os: OutputStream?,
        `is`: InputStream?
    ): Boolean {
        if (os == null || `is` == null) {
            return false
        }
        var read = 0
        while (true) {
            return try {
                val buffer = ByteArray(1444)
                while (`is`.read(buffer).also { read = it } != -1) {
                    os.write(buffer, 0, read)
                    os.flush()
                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            } finally {
                try {
                    os.close()
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getUri(context: Context, url: String): Uri? {
        val tempFile = File(url)
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //如果在Android7.0以上,使用FileProvider获取Uri
            try {
                val provider = AppUtils.getAppPackageName() + ".provider"
                return FileProvider.getUriForFile(context, provider , tempFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            //否则使用Uri.fromFile(file)方法获取Uri
            return Uri.fromFile(tempFile)
        }
        return null
    }
}