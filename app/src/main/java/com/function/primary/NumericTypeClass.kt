package com.function.primary

/**
 * @author Zero degree
 * @date 2021/2/3 15:32
 * @功能: 初级篇（三）：数据类型详解
 * https://www.cnblogs.com/Jetictors/p/7722417.html
 * /**
 * 1、Kotlin中的数字的内置类型（接近与Java），其关键字为：
 * Byte=> 字节 => 8位
 * Short => 短整型 => 16位
 * Int => 整型 => 32位
 * Long => 长整型 => 64位
 * Float => 浮点型 => 32位
 * Double => 双精度浮点型 => 64位
 *
 * */
 */


class NumericTypeClass {
    /**
    较小的类型不会被隐式转换为更大的类型，故而系统提供了显式转换。提供的显式转换方法如下：

    toByte() => 转换为字节型
    toShort() => 转换为短整型
    toInt() => 转换为整型
    toLong() => 转换为长整型
    toFloat() => 转换为浮点型
    toDouble() => 转换为双精度浮点型
    toChar() => 转换为字符型
    toString() => 转换为字符串型

     */
    fun testConversion() {
        var numA: Int = 97
        println(numA.toByte())
        println(numA.toShort())
        println(numA.toInt())
        println(numA.toLong())
        println(numA.toFloat())
        println(numA.toDouble())
        println(numA.toChar())
        println(numA.toString())
    }


    /**
    Kotlin中对于按位操作，和Java是有很大的差别的。Kotlin中没有特殊的字符，但是只能命名为可以以中缀形式调用的函数，下列是按位操作的完整列表(仅适用于整形（Int）和长整形（Long）)：
    shl(bits) => 有符号向左移 (类似Java的<<)
    shr(bits) => 有符号向右移 (类似Java的>>)
    ushr(bits) => 无符号向右移 (类似Java的>>>)
    and(bits) => 位运算符 and (同Java中的按位与)
    or(bits) => 位运算符 or (同Java中的按位或)
    xor(bits) => 位运算符 xor (同Java中的按位异或)
    inv() => 位运算符 按位取反 (同Java中的按位取反)
     */
    fun testOperator() {
        /*
    位运算符
    支持序列如下：shl、shr、ushr、and、or、xor
 */
        var operaNum: Int = 4

        var shlOperaNum = operaNum shl (2)
        var shrOperaNum = operaNum shr (2)
        var ushrOperaNum = operaNum ushr (2)
        var andOperaNum = operaNum and (2)
        var orOperaNum = operaNum or (2)
        var xorOperaNum = operaNum xor (2)
        var invOperaNum = operaNum.inv()

        println(
            "shlOperaNum => $shlOperaNum \n " +
                    "shrOperaNum => $shrOperaNum \n " +
                    "ushrOperaNum => $ushrOperaNum \n " +
                    "andOperaNum => $andOperaNum \n " +
                    "orOperaNum => $orOperaNum \n " +
                    "xorOperaNum => $xorOperaNum \n " +
                    "invOperaNum => $invOperaNum"
        )
    }

    /**
    1、关键字
    String表示字符串类型。其是不可变的。所以字符串的元素可以通过索引操作的字符：str[index]来访问。可以使用for循环迭代字符串：
    其中str[index]中的str为要目标字符串，index为索引
     */
    fun testString(){
        val str: String = "kotlin"
        println("str => $str")

        //迭代
        for (s in str){
            print(s)
            print("\t")
        }
    }

    /**
    Kotlin中数组由Array<T>表示，可以去看看源码实现，里面就几个方法
    创建数组的3个函数
    arrayOf()
    arrayOfNulls()
    工厂函数（Array()）
     */
    fun testArray(){
        //1、arrayOf() 创建一个数组，参数是一个可变参数的泛型对象
        var arr1 = arrayOf(1,2,3,4,5) //等价于[1,2,3,4,5]
        for (v in arr1){
            print(v)
            print("\t")
        }

        var arr2 = arrayOf("0","2","3",'a',32.3f)
        for (v in arr2){
            print(v)
            print("\t")
        }
    }
}