package com.jacky.wanandroidkotlin.test

import kotlinx.coroutines.*

/**
 * @author:Hzj
 * @date  :2020/8/4
 * desc  ：协程练习
 * record：
 */
//fun main()= runBlocking {
//    launch {//开启一个协程
//        delay(2000)
//        println("task from run blocking")
//    }
//    coroutineScope {//创建一个协程作用域
//        launch {
//            delay(5000)
//            println("task from nested launch")
//        }
//        delay(1000)
//        println("Task from coroutine scope") // 这一行会在内嵌 launch 之前输出
//    }
//    println("coroutineScope is over")//这一行在内嵌 launch 执行完之后才输出
//}

fun main() = runBlocking {
    testCoroutine()
}

fun testCoroutine() {
    GlobalScope.launch {
        println("1:${Thread.currentThread().name}")
        //启动一个子协程
        val job = launch {
            println("2:${Thread.currentThread().name}")
            try {//捕获协程cancel 导致的异常
                delay(1000)
            } catch (e: Exception) {
                println("error=${e.message}:${Thread.currentThread().name}")
            }
            println("3:${Thread.currentThread().name}")
            if (isActive) { //如果协程cancel了，isActive为false，用于检查协程是否已取消
                println("4:${Thread.currentThread().name}")
            }
            delay(1000)//没有捕获异常，终止代码继续往下执行
            println("5:${Thread.currentThread().name}")
        }
        delay(100)
        job.cancel()//jon.cancel 它们检查协程的取消， 并在取消时抛出 CancellationException
//        job.cancelAndJoin()//job的取消必须和join一同执行，否则抛异常
    }
    Thread.sleep(5000)// 阻塞主线程 5 秒钟来保证 JVM 存活
}