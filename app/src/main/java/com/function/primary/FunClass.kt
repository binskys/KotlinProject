package com.function.primary

import java.lang.Integer.parseInt
import java.util.*

/**
 * @author Zero degree
 * @date 2021/2/2 17:26
 * @功能: 函数定义
 */

class FunClass {
    /**
     * 函数
     */
    fun sum(a: Int, b: Int): Int {   // Int 参数，返回值 Int
        return a + b
    }

    /**
     * 条件表达式
     */
    fun maxOf(a: Int, b: Int): Int {
        if (a > b) {
            return a
        } else {
            return b
        }
    }

    /**
     *空值与 null 检测
     */
    fun printProduct(arg1: String, arg2: String) {
        val x = parseInt(arg1)
        val y = parseInt(arg2)

        // 直接使用 `x * y` 会导致编译错误，因为它们可能为 null
        if (x != null && y != null) {
            // 在空检测后，x 与 y 会自动转换为非空值（non-nullable）
            println(x * y)
        }
        else {
            println("'$arg1' or '$arg2' is not a number")
        }
    }

    /**
     * 遍历
     */
    fun printMap() {
        var map:Map<Int,String>?=null
        map= hashMapOf()
        map[1]="benny"
        map[2]="18"
        //或
        var map2= mapOf("area" to "江西")

        for ((k, v) in map2) {
            println("$k -> $v")
        }
    }
}