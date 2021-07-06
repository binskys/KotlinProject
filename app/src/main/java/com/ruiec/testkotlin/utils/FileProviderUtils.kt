package com.ruiec.testkotlin.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 *
 * @author pfm
 * @date 2021/3/11 11:51
 */
object FileProviderUtils {
    /**
     * 从文件获得URI * @param context 上下文 * @param file 文件 * @return 文件对应的URI
     */
    private fun uriFromFile(context: Context, file: File?): Uri? {
        val fileUri: Uri
        //7.0以上进行适配
        fileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val p = context.packageName + ".fileprovider"
            FileProvider.getUriForFile(context, p, file!!)
        } else {
            Uri.fromFile(file)
        }
        return fileUri
    }

    /**
     * 设置Intent的data和类型，并赋予目标程序临时的URI读写权限
     * * @param context 上下文
     * * @param intent 意图
     * * @param type 类型
     * * @param file 文件
     * * @param writeAble 是否赋予可写URI的权限
     */
    fun setIntentDataAndType(
        context: Activity,
        intent: Intent,
        type: String?,
        file: File?,
        writeAble: Boolean
    ) {
        //7.0以上进行适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(uriFromFile(context, file), type)
            //临时赋予读写Uri的权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
        }
    }

    /**
     * 设置Intent的data和类型，并赋予目标程序临时的URI读写权限
     * @param context 上下文
     * @param intent 意图
     * @param type 类型
     * @param fileUri 文件uri
     * @param writeAble 是否赋予可写URI的权限
     */
    fun setIntentDataAndType(
        context: Context?,
        intent: Intent,
        type: String?,
        fileUri: Uri?,
        writeAble: Boolean
    ) {
        //7.0以上进行适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(fileUri, type)
            //临时赋予读写Uri的权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        } else {
            intent.setDataAndType(fileUri, type)
        }
    }
}