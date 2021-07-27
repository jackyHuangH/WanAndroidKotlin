package com.jacky.wanandroidkotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

/**
 * @author:Hzj
 * @date  :2021/7/27
 * desc  ：Kotlin Flow使用
 * record：flow 是 Cold Stream。在没有切换线程的情况下，生产者和消费者是同步非阻塞的。
 * channel 是 Hot Stream。而 channelFlow 实现了生产者和消费者异步非阻塞模型
 */
fun main() = runBlocking {
    launch {
        for (i in 1..5){
            delay(100)
            println("I'm not bloked:$i")
        }
    }

    flow {
        for (i in 1..5){
            delay(100)
            emit(i)
        }
    }.collect {
        println(it)
    }

    //Java sequence会阻塞主线程
    sequence {
        for (i in 6..10){
            Thread.sleep(100)
            yield(i)
        }
    }.forEach { println(it) }

    println("Done")
//    createFlow()
//    createChannel()
}

suspend fun createFlow() {
    //flowOf()
//    flowOf(1, 2, 3, 4, 5)
    //asFlow()
    val time = measureTimeMillis {
        listOf(1, 2, 3, 4, 5).asFlow()
            .onEach {
                delay(100)
            }
            .flowOn(Dispatchers.IO)
            .collect {
                delay(100)
                println(it)
            }
    }
    println("flow thread:${Thread.currentThread().name} ,cost time:$time")
}

suspend fun createChannel()= withContext(Dispatchers.IO) {
    val time = measureTimeMillis {
        channelFlow {
            for (i in 1..5) {
                delay(100)
                send(i)
            }
        }
            .collect {
            delay(100)
            println(it)
        }
    }
    println("channel thread:${Thread.currentThread().name} ,cost time:$time")
}