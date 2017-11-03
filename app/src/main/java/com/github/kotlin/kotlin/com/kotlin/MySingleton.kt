package com.github.kotlin.kotlin.com.kotlin

/**
 * @author benny
 * @Date 2017/11/2
 * @function
 */
class MySingleton private constructor(a:Int){
    var bbb=a


    public var vaule:MySingleton?=null
    private object mTest{
        var install=MySingleton(10)
    }
    companion object myTest{
        fun getInstace():MySingleton{
            return mTest.install
        }
    }
}