package com.function.advanced

import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

/**
 * @author Zero degree
 * @date 2021/2/3 11:27
 * @功能: 协程
 */

fun main2(args: Array<String>) {
    var testClass = CoroutinesClass()
    testClass
}

class CoroutinesClass {
    init {
        //轻量级的线程
        globalScope()
        Thread.sleep(4000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
        thread()
    }

    fun globalScope() {
        GlobalScope.launch { // 在后台启动一个新的协程并继续
            delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
            println("World!_1") // 在延迟后打印输出
        }
        println("Hello,") // 协程已在等待时主线程还在继续

    }

    fun thread() {
        thread { // 在后台启动一个新的协程并继续
            Thread.sleep(4000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
            println("World!_2") // 在延迟后打印输出
        }
        println("Hello,") // 协程已在等待时主线程还在继续
    }


}

/**
 * 组合挂起函数 runBlocking 主协程(默认顺序调用)
 */
fun main3()= runBlocking{
    val time = measureTimeMillis {
        val one = doSomethingUsefulOne()

        val two = doSomethingUsefulTwo()
        println("The answer is ${one}")
        println("The answer is ${two}")
    }
    println("Completed in $time ms")


}

/**
 * 组合挂起函数 runBlocking 主协程(使用 async 并发)
 */
fun main()= runBlocking{
    val time = measureTimeMillis {
        val one =async { doSomethingUsefulOne() }

        val two = async { doSomethingUsefulTwo()}
        println("The answer is ${one.await()}")
        println("The answer is ${two.await()}")
    }
    println("Completed in $time ms")

    //惰性启动的 async
    val time2 = measureTimeMillis {
        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
        // 执行一些计算
        one.start() // 启动第一个
        two.start() // 启动第二个
        println("The answer is ${one.await() + two.await()}")
    }
    println("Completed in $time2 ms")

}

suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // 假设我们在这里做了一些有用的事
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // 假设我们在这里也做了一些有用的事
    return 29
}

/**
 * async 风格的函数
 */
// 注意，在这个示例中我们在 `main` 函数的右边没有加上 `runBlocking`
fun main5() {
    val time = measureTimeMillis {
        // 我们可以在协程外面启动异步执行
        val one = somethingUsefulOneAsync()
        val two = somethingUsefulTwoAsync()
        // 但是等待结果必须调用其它的挂起或者阻塞
        // 当我们等待结果的时候，这里我们使用 `runBlocking { …… }` 来阻塞主线程
        runBlocking {
            println("The answer is ${one.await() + two.await()}")
        }
    }
    println("Completed in $time ms")
}

// somethingUsefulOneAsync 函数的返回值类型是 Deferred<Int>
fun somethingUsefulOneAsync() = GlobalScope.async {
    doSomethingUsefulOne()
}

// somethingUsefulTwoAsync 函数的返回值类型是 Deferred<Int>
fun somethingUsefulTwoAsync() = GlobalScope.async {
    doSomethingUsefulTwo()
}

