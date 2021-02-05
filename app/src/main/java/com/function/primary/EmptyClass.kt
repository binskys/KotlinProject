package com.function.primary

/**
 * @author Zero degree
 * @date 2021/2/5 17:28
 * @功能: 空类型、空安全、非空断言、类型转换等特性
 */

class EmptyClass {
    fun emptyType(){
        /*
    定义可空类型的变量,即变量可以被赋值为null
    定义格式为：修饰符 变量名 ： 类型? = 值
*/
        var nullA : Int? = 12
        val nullB : Int? = 13

        nullA = null

        if(nullA == null){
            println("nullA = $nullA")
        }

        //1.2.2、使用符号?.判断
        var str : String? = "123456"
        str = null
        println("str ="+str?.length)   // 当变量str为null时，会返回空(null)
    }

    /**
     * 1.2.3、链式调用
     */
    class Builder{
        private var name : String? = "Tom"
        private var age : Int? = 0
        private var sex : String? = "男"

        fun setName(name : String) : Builder?{
            this.name = name
            return this
        }

        fun setAge(age : Int) : Builder?{
            this.age = age
            return this
        }

        fun setSex(sex: String?) : Builder?{
            this.sex = sex
            return this
        }

        override fun toString(): String {
            return "Builder(name=$name, age=$age, sex=$sex)"
        }
    }

    fun testBuilder(){
        val builder : EmptyClass.Builder? = EmptyClass.Builder().setName("Lily")?.setSex("nv")?.setAge(10)
        println(builder.toString())
    }
}