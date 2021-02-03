package com.function.primary

/**
 * @author Zero degree
 * @date 2021/2/2 16:13
 * @功能:Kotlin——初级篇（二）：变量、常量、注释
 * https://www.cnblogs.com/Jetictors/p/7723044.html
 */



/**
 * 常量 二、Kotlin之常量的用法
 * Kotlin中的 val numA = 6   等价于  Java中的：public final int numA = 6
 * 很显然，Kotlin中只用val修饰还不是常量，它只能是一个不能修改的变量。那么常量怎么定义呢？其实很简单，在val关键字前面加上const关键字。
 * 其特点：const只能修饰val，不能修饰var
 */
// 1. 顶层声明
const val NUM_A : String = "顶层声明"

// 2. 在object修饰的类中
object TestConst {
    const val NUM_B = "object修饰的类中"
}
// 3. 伴生对象中
class TestClass{
    companion object {
        const val NUM_C = "伴生对象中声明"
    }
}

/**
 *  Kotlin之变量用法
 */
class VariableClass {
    fun test(s: String) {
        println(s)
    }

    //定义属性
    var var_a: Int = 10

    //推导出类型
    var var_b = 5


    // 声明可空变量

    var var_a2 : Int? = 0
    val val_a2 : Int? = null
    init {
        var_a = 10
        // val_a = 0 为val类型不能更改。

        println("var_a => $var_a \t val_a => $val_a2")

        //没有初始化的时候，必须声明类型
        var var_c: Float
        var_c = 12.3f
        var_c += 1

        println("var_a => $var_a \t var_b => $var_b \t var_a => $var_c")

        //立即初始化
        val val_a: Int = 100

        //推导出类型
        val val_b = 50

        //没有初始化的时候，必须声明类型
        val val_c: Int
        val_c = 1
// val_c += 1 因为c是常量，所以这句代码是会报错的

        println("val_a => $val_a \t val_b => $val_b \t val_c => $val_c")
    }
}
