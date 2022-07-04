package com.jacky.wanandroidkotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

/**
 * @author:Hzj
 * @date  :2019/6/24/024
 * desc  ：协程学习 官方文档：http://www.kotlincn.net/docs/reference/coroutines/channels.html
 * record：//第一个协程
 * 在 Kotlin 中有一个约定：如果函数的最后一个参数接受函数，那么作为相应参数传入的 lambda 表达式可以放在圆括号之外(即在{}中)
 * 如果该 lambda 表达式是调用时唯一的参数，那么圆括号可以完全省略
 */
object CoroutineTest {
    @Test
    fun testMySuspendingFunction() = runBlocking<Unit> {
        // 这里我们可以通过任何我们喜欢的断言风格使用挂起函数
    }

    @Test
    fun testChangeThread() {
        //线程间切换,withContext函数切换协程的上下文，但依然是在相同的协程中执行
        newSingleThreadContext("ctx1").use { ctx1 ->
            newSingleThreadContext("ctx2").use { ctx2 ->
                runBlocking(ctx1) {
                    log("started in ctx1")
                    withContext(ctx2) {
                        log("work in ctx2")
                    }
                    log("back to ctx1")
                }
            }
        }
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

suspend fun calOne(): Int {
    delay(1000L)//模拟耗时
    return 1
}

suspend fun calTwo(): Int {
    delay(1000L)
    return 2
}

//调试协程，打印协程名称
fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

//互斥操作,并发操作加锁
val mutex = Mutex()

//共享可变状态与并发
suspend fun massiveRun(context: CoroutineContext, action: suspend CoroutineScope.() -> Unit) {
    val n = 1000 // 启动协程的数量
    val k = 1000 // 每个协程执行动作的次数
    val time = measureTimeMillis {
        val jobs = List(n) {
            GlobalScope.launch(context) {
                repeat(k) { action() }
            }
        }
        jobs.forEach { it.join() }
    }
    println("Completed ${n * k} actions in $time ms")
}

val mtContext = newFixedThreadPoolContext(2, "mtPool") // 定义一个2线程的上下文

//---------------------------------------------------------------
//协程执行顺序，挂起
suspend fun getToken(): String {
    delay(2L)
    println("getToken 开始执行，时间:  ${System.currentTimeMillis()}")
    return "ask"
}

suspend fun getResponse(token: String): String {
    delay(1L)
    println("getResponse 开始执行，时间:  ${System.currentTimeMillis()}")
    return "response"
}

fun setText(response: String) {
    println("setText 执行，时间:  ${System.currentTimeMillis()}")
}

//=-------------------------------------------------------

class CoroutineUnitTest {

    @Test
    fun main1() = runBlocking<Unit> {
        //多协程并行执行


        //---------------------------------------------
        //协程执行顺序
        /*val token = GlobalScope.async {
            return@async getToken()
        }.await()

        val response = GlobalScope.async {
            return@async getResponse(token)
        }.await()

        setText(response)*/

        println("协程 开始执行，时间:  ${System.currentTimeMillis()}")
        val token = getToken()
        val response = getResponse(token)
        setText(response)

//    GlobalScope.launch(Dispatchers.Default) {
//        for (a in 1..8) {
//            println("协程任务打印第$a 次，时间: ${System.currentTimeMillis()}")
//        }
//    }

        for (i in 1..10) {
            println("主线程打印第$i 次，时间:  ${System.currentTimeMillis()}")
        }

        //-----------------------------------------------

        //共享可变状态与并发
//    var num = 0
//    massiveRun(mtContext) {
//        mutex.withLock {
//            num++
//        }
//    }
//    println("num=$num")
        //------------------------------------------

        //通道Channel,延期的值提供了一种便捷的方法使单个值在多个协程之间进行相互传输。 通道提供了一种 *在流中传输值的方法*
        //一个 Channel 是一个和 BlockingQueue 非常相似的概念。其中一个不同是它代替了阻塞的 put 操作并提供了挂起的 send，
        // 还替代了阻塞的 take 操作并提供了挂起的 receive
//    val channel = Channel<Int>()
//    launch {
//        // 这里可能是消耗大量 CPU 运算的异步逻辑，我们将仅仅做 5 次整数的平方并发送
//        for (x in 1..5) {
//            channel.send(x * x)
//            if (x > 3) {
//                //关闭通道，结束发送
//                channel.close()
//            }
//        }
//    }
        // 这里我们打印了 5 次被接收的整数：
//    repeat(5) {
//        println(channel.receive())
//    }
        // 这里我们使用 `for` 循环来打印所有被接收到的元素（直到通道被关闭）
//    for (y in channel) println(y)
//    println("done!")

        //-------------------------------------------------------

        //子协程当一个协程被其它协程在 CoroutineScope 中启动的时候， 它将通过 CoroutineScope.coroutineContext 来承袭上下文，
        // 并且这个新协程的 Job 将会成为父协程作业的 子 作业。当一个父协程被取消的时候，所有它的子协程也会被递归的取消。
//    val fatherRequest = launch {
//        // 孵化了两个子作业, 其中一个通过 GlobalScope 启动
//        GlobalScope.launch {
//            println("job1: I run in GlobalScope and execute independently!")
//            delay(1000)
//            println("job1: I am not affected by cancellation of the request")
//        }
//        //另一个则承袭了父协程的上下文
//        launch {
//            delay(100)
//            println("job2: I am a child of the request coroutine")
//            delay(1000)
//            println("job2: I will not execute this line if my parent request is cancelled")
//        }
//        coroutineScope {  }
//        MainScope()
//    }
//    delay(500)
//    fatherRequest.cancel()//取消request请求
//    delay(1000)
//    println("main: Who has survived request cancellation?")
        //-------------------------------------
//    CoroutineTest.testChangeThread()

        //------------------------------------------------
        //调试协程和线程
//    val a = async(coroutineContext) {
//        log("I am doing something")
//        6
//    }
//    val b = async(coroutineContext) {
//        log("I am doing some other thing")
//        9
//    }
//    println("My job is ${coroutineContext[Job]}")
//    log("the answer is ${a.await()+b.await()}")
        //-----------------------------------------------------

        //-调度器
//    val job1 = GlobalScope.launch {
//        // 运行在父协程的上下文中，即 runBlocking 主协程
//        println("main runBlocking:current thread is ${Thread.currentThread().name}")
//        delay(1000L)
//        println("main runBlocking:after delay current thread is ${Thread.currentThread().name}")
//    }
//
//    val job2 = GlobalScope.launch(Dispatchers.Unconfined) {
//        // 不受限的,将工作在主线程中
//        println("Unconfined:current thread is ${Thread.currentThread().name}")
//        delay(500L)
//        println("Unconfined:after delay current thread is ${Thread.currentThread().name}")
//    }
//    job1.join()
//    job2.join()

        /* GlobalScope.launch(Dispatchers.Default) {
             // 将会获取默认调度器
             println("Default:current thread is ${Thread.currentThread().name}")
         }
         GlobalScope.launch(newSingleThreadContext("newThread")) {
             //获得一个新的线程
             println("newSingleThreadContext:current thread is ${Thread.currentThread().name}")
         }*/

        //--------------------------------------------------------

        //函数类型实例化
//    val stringPlus: (String, String) -> String = String::plus
//    val intPlus: Int.(Int) -> Int = Int::plus
//
//    //函数类型的值可以通过其 invoke(……) 操作符调用：f.invoke(x) 或者直接 f(x)
//    println(stringPlus("--", "22"))
//    println(stringPlus.invoke("nihao", "背景"))
//    println(intPlus(3, 9))
//    println(intPlus.invoke(4, 9))
//    //// 类扩展调用
//    println(8.intPlus(9))

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
//    val time = measureTimeMillis {
//        //协程并发执行
////        val calOne = async(start = CoroutineStart.LAZY) { calOne() }
////        val calTwo = async(start = CoroutineStart.LAZY) { calTwo() }
//
//        val calOneAsync = calOneAsync()
//        val calTwoAsync = calTwoAsync()
//        //执行异步方法时，要在runBlock{}阻塞主线程来获取结果
//
//        println("cal total:${calOneAsync.await() + calTwoAsync.await()}")
//    }
//    println("time:$time")


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
}
