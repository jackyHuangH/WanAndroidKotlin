package com.jacky.wanandroidkotlin

import kotlinx.coroutines.*
import org.junit.Test

/**
 * @author:Hzj
 * @date  :2022/7/4
 * desc  ：普通协程范围，子协程出异常，父协程都会被取消,使用SupervisorJob定义scope，不会影响父协程
 * record：
 */

class SupervisorJobTest {

    @Test
    fun testJob() = runBlocking {
        val workManager = WorkManager()

//        var work1 = 0
//        var work2 = 0
//        work1 = workManager.doWork1().await()
//        println("work1 result $work1")
//
//        try {
//            work2 = workManager.doWork2().await()
//            println("work2 result $work2")
//        } catch (e: Exception) {
//            println("dowork2 catch $e")
//        }
//        println("final: ${work1 + work2}")

        workManager.doWork3()
        workManager.doWork4()
        //使用job.cancel时，后续dowork3不会执行；使用scope.coroutineContext.cancelChildren取消协程，后续dowork3正常执行
        workManager.cancelAllWork()
        workManager.doWork3()
    }
}

/**
 * 对Job进行cancel操作
如果想取消当前启动的所有子协程，同时不影响后续的新协程的启动，应该使用CoroutineContext.cancelChildren()

对Job进行cancel，Job关联的所有子协程都将停止的同时，Job变为Completed状态，此后无法再用此Job启动协程
 */
class WorkManager {

    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + job)


    suspend fun doWork1(): Deferred<Int> = scope.async {
        //模拟耗时
        delay(50)
        56
    }

    suspend fun doWork2(): Deferred<Int> = scope.async {
        //模拟耗时
        delay(30)
        throw ArithmeticException()
        21
    }

    fun doWork3() {
        scope.launch {
            println("work3")
        }
    }

    fun doWork4() {
        scope.launch {
            println("work4")
        }
    }

    fun cancelAllWork() {
//        job.cancel()//以后再起的job无法工作
        scope.coroutineContext.cancelChildren()//以后再起来的job可以工作
    }
}
