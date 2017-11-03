package com.github.kotlin.kotlin

import com.github.kotlin.kotlin.com.kotlin.*

/**
 * @author  benny
 * on 2017/10/26.
 * 功能：
 */

fun main(args: Array<String>) {
//   onArray()
//   onIntArray()
   var a="hkfdjghdfhg0"
  //println("my name is ${getName()}")
  //println("my age is ${Student().getAge()}")
//onIf()
  //  onWhen()
  //  onSingleton()
    onConstructor()
}
fun onConstructor(){
    var  num=ConstructorTest(99)
    var  str=ConstructorTest("this")
    println(num)
    println(str)
}
fun onSingleton(){
    var ob=MySingleton.getInstace()
    println("ob is $ob")
}
fun onWhen(){
    var x=1
    when(x){
        1->{

        }
        2->{

        }

    }
}
fun onArray(){
   var str= arrayOf(1,6,5,9,"a")
   for (item:Any in str){
      println(item)
   }
}
fun onIntArray(){
   var str:IntArray= intArrayOf(20,30,60,50,90)
    for (item:Int in str){
   println(item)
}
}
fun onIf(){
    var a=30
    var b=40;
    var temp=if (a>b)a else b
    println("temp is $temp")
}


