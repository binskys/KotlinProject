package com.github.kotlin.kotlin.com.kotlin

/**
 * @author benny
 * @Date 2017/11/2
 * @function
 */
class Person constructor(name:String) {
    var name=name

    /**
     * 主构造器
     */
    init {
        println("name is $name")
    }
    constructor(age:Int):this(""){

    }

}