package com.jacky.wanandroidkotlin

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

/**
 * @author:Hzj
 * @date  :2019/6/24/024
 * desc  ：协程学习 官方文档：http://www.kotlincn.net/docs/reference/coroutines/channels.html
 * record：//第一个协程
 * 在 Kotlin 中有一个约定：如果函数的最后一个参数接受函数，那么作为相应参数传入的 lambda 表达式可以放在圆括号之外(即在{}中)
 * 如果该 lambda 表达式是调用时唯一的参数，那么圆括号可以完全省略
 */
class CoroutineTest {
    @Test
    fun testMySuspendingFunction() = runBlocking<Unit> {
        // 这里我们可以通过任何我们喜欢的断言风格使用挂起函数

    }

}

//将耗时操作抽取成挂起函数
//挂起函数可用于协程中，就像使用普通函数一样，但是它们有额外的特性——可以调用其他的挂起函数去挂起协程的执行
suspend fun doWorld() {
    delay(1000L)
    println("协程挂起函数")
}

//用async协程建造器定义异步风格的函数
fun calOneAsync() = GlobalScope.async {
    calOne()
}

fun calTwoAsync() = GlobalScope.async {
    calTwo()
}

fun main(args: Array<String>) = runBlocking<Unit> {

    //函数类型实例化
    val stringPlus: (String, String) -> String = String::plus
    val intPlus: Int.(Int) -> Int = Int::plus

    //函数类型的值可以通过其 invoke(……) 操作符调用：f.invoke(x) 或者直接 f(x)
    println(stringPlus("--", "22"))
    println(stringPlus.invoke("nihao", "背景"))
    println(intPlus(3, 9))
    println(intPlus.invoke(4, 9))
    //// 类扩展调用
    println(8.intPlus(9))

    //--------------------------------------------------------------------------

//    val repeatFun: String.(Int) -> String = { times -> this.repeat(times) }
//    val twoParameters: (String, Int) -> String = repeatFun // OK
//    fun runTransformation(f: (String, Int) -> String): String {
//        return f("hello", 3)
//    }
//    val result = runTransformation(repeatFun) // OK
//    val result2 = runTransformation(twoParameters) // OK
//    println("result = $result,result2=$result2")

    //--------------------------------------------------------------------------

    //launch返回了一个不携带任何结果的Job，但是async返回了一个Deferred，一个轻量级非阻塞的future，
    // 表示一会就会返回结果的承诺。你可以在一个延期的值（deferred value）使用.await()来获取最终的结果，
    // 但Deferred也是个Job， 因此，需要的话，你也可以取消掉。
    //
    val time = measureTimeMillis {
        //协程并发执行
//        val calOne = async(start = CoroutineStart.LAZY) { calOne() }
//        val calTwo = async(start = CoroutineStart.LAZY) { calTwo() }

        val calOneAsync = calOneAsync()
        val calTwoAsync = calTwoAsync()
        //执行异步方法时，要在runBlock{}阻塞主线程来获取结果

        println("cal total:${calOneAsync.await() + calTwoAsync.await()}")
    }
    println("time:$time")


    // 在后台启动一个新的协程，然后继续执行
    /*GlobalScope.launch(Dispatchers.Default) {
        delay(1000L)// 不阻塞的延迟1s
        println("world!")// 延迟后打印
    }
    print("hello,")// 当协程延迟时，主线程还在跑

    // 阻塞主线程2s，为了让jvm不挂掉
    // Thread.sleep(2000L)
    //我们使用runBlocking协程建造器，明确指明阻塞：
    delay(2000L)*/


    // 等待任务（Job）
    //当另一个协程在运行时，延迟一段时间并不是一个好办法。让我们明确的等待（非阻塞的方式），直到我们启动的后台任务完成
//   val job= GlobalScope.launch {
////        delay(1000L)
////        print("协程！")
//       doWorld()
//    }
//    println("你好，")
//    job.join()//等待子协程完成

    //这里启动了十万个协程，一秒之后，每个协程打印了一个点。你用线程试试？（很有可能就OOM了）
    // 启动大量的协程，并返回它们的任务
    /*val jobs = List(100_000) {
        GlobalScope.launch {
            delay(1000L)
            println(".")
        }
    }
    // 等待其他全部的任务完成
    jobs.forEach { it.join() }*/

    //协程像守护线程
    //协程的取消需要配合，满足条件才会取消，否则要执行完才会自己结束
//    val startTime = System.currentTimeMillis()
//    val job = GlobalScope.launch(Dispatchers.Default) {
//        var nextPrintTime = startTime
//        var i = 0
//        while (isActive) {
//            if (System.currentTimeMillis() >= nextPrintTime) {
//                println("job:I am sleeping ${i++}...")
//                nextPrintTime += 500L
//            }
//        }
//    }
//    delay(1300L)//延迟后退出
//    println("main:我不想再等了")
////    job.cancel()//取消协程任务
////    job.join()//等待任务完成,任务完成了才能继续执行协程后面的代码
//    job.cancelAndJoin()//相当于上面两个函数同时调用
//    println("main:now i am quit")

    //用finally释放资源
    //可取消的挂起函数在取消时会抛出CancellationException，通常的方式就可以处理了。
    // 例如，try {...} finally {...}表达式或use函数，会在协程取消时，执行结束动作。
//    val job = GlobalScope.launch {
//        try {
//            repeat(1000) { i ->
//                println("job:i am sleeping $i ...")
//                delay(500L)
//            }
//        } finally {
//            withContext(NonCancellable) {
//                println("job:i'm run finally")
//                delay(1000L)
//                println("And I've just delayed for 1 sec because I'm non-cancellable")
//            }
//        }
//    }
//    delay(1300L)//延迟后退出
//    println("main:我不想再等了")
//    job.cancelAndJoin()//取消任务并等待结束
//    println("main:now i am quit")

    //超时withTimeOut 抛出TimeoutCancellationException
    //withTimeoutOrNull，返回null而不是抛出异常：
//    val result = withTimeoutOrNull(1300L) {
//        repeat(1000) { i ->
//            println("I'm sleeping $i ...")
//            delay(500L)
//        }
//        "done"// will get cancelled before it produces this result
//    }
//    println("result:$result")

}

suspend fun calOne(): Int {
    delay(1000L)//模拟耗时
    return 1
}

suspend fun calTwo(): Int {
    delay(1000L)
    return 2
}
