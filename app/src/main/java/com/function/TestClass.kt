package com.function

import android.widget.Toast
import com.function.primary.*
import kotlinx.coroutines.*
import java.util.*

/**
 * @author Zero degree
 * @date 2021/2/2 16:16
 * @功能:
 */

fun main(args: Array<String>) {
    printNumericType()
    //printConst()
   // printVariable()
    //printFun()
}

/**
 * 数据类型详解
 */
fun printNumericType() {


}

fun printFun() {
    val funClass = FunClass()
    val num = funClass.sum(2, 3)
    val maxOf = funClass.maxOf(2, 3)
    funClass.printProduct("2", "0")
    funClass.printMap()

    println(num.toString() + "\t" + maxOf)

}

fun printVariable() {
    val mVariable = VariableClass()

}

fun printConst() {
    println("NUM_A => ${NUM_A}")
    println("NUM_B => ${TestConst.NUM_B}")
    println("NUM_C => ${TestClass.NUM_C}")

}
