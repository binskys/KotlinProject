package com.function.primary

/**
 * @author Zero degree
 * @date 2021/2/5 16:46
 * @功能: 控制语句详解
 */

class ControlClass {
    fun testIf() {
        //1、传统写法（同Java写法一样）
        var numA = 2
        if (numA == 2) {
            println("numA == $numA => true")
        } else {
            println("numA == $numA => false")
        }
        //2、Kotlin中的三元运算符
        // kotlin中直接用if..else替代。例：
        var numB: Int = if (numA > 2) 3 else 5  // 当numA大于2时输出numB的值为3，反之为5
        println("numB = > $numB")
    }

    fun testFor() {
        //循环5次，且步长为1的递增
        for (i in 0 until 5) {
            println("i => $i")
        }
        //循环5次，且步长为2的递增
        for (i in 0 until 5 step 2) {
            println("i => $i")
        }
    }

    /**
    在Kotlin中已经废除掉了Java中的switch语句。而新增了when(exp){}语句。
    when语句不仅可以替代掉switch语句，而且比switch语句更加强大
     */
    fun testWhen() {
        when(5){
            1 -> {
                println("1")
            }
            2 -> println("2")
            3 -> println("3")
            5 -> {
                println("5")
            }
            else -> {
                println("0")
            }
        }

    }
}