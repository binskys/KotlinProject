package com.ruiec.common_library.util

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 *
 * @author pfm
 * @date 2021/1/29 16:24
 */
object LogUtils {

    //换行符
    private var LINE_SEPARATOR = System.getProperty("line.separator")

    private fun printLine(tag : String, isTop : Boolean) {
        if (isTop) {
            Log.d(
                tag,
                "╔═══════════════════════════════════════════════════════════════════════════════════════"
            )
        } else {
            Log.d(
                tag,
                "╚═══════════════════════════════════════════════════════════════════════════════════════"
            )
        }
    }

    //将JSON打印格式化
    fun printJson(tag : String , msg : String) {
        var message: String
        message = try {
            when {
                msg.startsWith("{") -> {
                    val jsonObject = JSONObject(msg)
                    jsonObject.toString(4)//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
                }
                msg.startsWith("[") -> {
                    val jsonArray = JSONArray(msg)
                    jsonArray.toString(4)
                }
                else -> {
                    msg
                }
            }
        } catch (e : JSONException) {
            msg
        }

        printLine(tag, true)
        message = LINE_SEPARATOR + message
        val lines  = message.split(LINE_SEPARATOR!!)
        for (line in lines) {
            Log.d(tag, "║ $line")
        }
        printLine(tag, false)
    }
}