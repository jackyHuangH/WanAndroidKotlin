package com.jacky.wanandroidkotlin.designmode

/**
 * @author:Hzj
 * @date  :2022/3/1
 * desc  ：装饰模式
 * record：
 */

interface Person{
    fun run()
}

class Man:Person{
    override fun run() {
        println("Man is running")
    }
}

abstract class Decorator(val person: Person):Person{

    override fun run() {
        println("我是装饰器加上的代码，准备开跑")
        person.run()
    }
}

class DecorImpl(person: Person):Decorator(person){
    override fun run() {
        super.run()
        println("run end")
    }
}

fun main() {
    val man=Man()
    val decorImpl=DecorImpl(man)
    decorImpl.run()
}