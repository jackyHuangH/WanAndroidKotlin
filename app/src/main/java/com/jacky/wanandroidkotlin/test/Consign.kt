package com.jacky.wanandroidkotlin.test

/**
 * @author:Hzj
 * @date  :2022/2/15
 * desc  ：kotlin 委托模式
 * record：委托模式已经证明是实现继承的⼀个很好的替代⽅式， ⽽ Kotlin 可以零样板代码地原⽣ ⽀持它
 */
interface Base {
    val msg: String
    fun say()
    fun init() {
        print("init..")
    }
}

class BaseImpl(val word: String) : Base {
    override val msg: String
        get() = "BaseImpl:word=$word"

    override fun say() {
        println(msg)
    }
}

class Derived(b: Base) : Base by b {
    //// 在 b 的 `print` 实现中不会访问到这个属性
    override val msg: String
        get() ="Derived msg"

    //Derived 的超类型列表中的 by -⼦句表示 b 将会在 Derived 中内部存储， 并且编译器将⽣成转发给 b 的所有 Base 的⽅法
    fun go() {
        println("derived inner")
//        init()
//        say()
    }
}


fun main() {
    val b = BaseImpl("你好")
    val derived = Derived(b)
    derived.init()
    derived.say()
    derived.go()
    //打印的是Base接口里的msg，Derived内部覆写的msg访问不到
    println(derived.msg)
}