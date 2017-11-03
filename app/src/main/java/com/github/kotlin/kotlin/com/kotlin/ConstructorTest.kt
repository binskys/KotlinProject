package com.github.kotlin.kotlin.com.kotlin

/**
 * @author benny
 * @Date 2017/11/3
 * @function
 */
class ConstructorTest (num:Int) {
    init {
        var bbb=num+6666
        println("第一构造器的bbb is $bbb")
    }
    constructor(j:String):this(999){
        println("第二构造器 $j")
    }
}