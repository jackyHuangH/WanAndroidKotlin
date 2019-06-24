package com.jacky.wanandroidkotlin.test

import kotlinx.coroutines.*

/**
 * @author:Hzj
 * @date  :2019/6/24/024
 * desc  ：协程学习
 * record：//第一个协程
 */
object CoroutineTest {
    fun runBlog() {
        //开启一个协程
        GlobalScope.launch(Dispatchers.Default) {
           var result= async {
                backJob()
            }
            var await = result.await()
            println("result=$await")
        }
    }

    suspend fun backJob():Int{
        //耗时操作
        delay(2000L)
        return 666
    }
}


fun main(args: Array<String>) {
    CoroutineTest.runBlog()
}