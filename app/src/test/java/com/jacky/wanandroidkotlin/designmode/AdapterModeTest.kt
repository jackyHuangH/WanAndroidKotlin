package com.jacky.wanandroidkotlin.designmode

/**
 * @author:Hzj
 * @date  :2022/2/23
 * desc  ：适配器模式
 * record：适配器模式将某个类的接口转换成客户端期望的另一个接口表示，目的是消除由于接口不匹配所造成的类的兼容性问题。
    主要分为三类：类的适配器模式、对象的适配器模式、接口的适配器模式
 */
open class Source{
    fun method(){
        println("我是source里的method")
    }
}


interface TargetTable{
    //与原类中方法保持一致
    fun method()
    //新方法
    fun method1()
}

//类的适配器模式
class Adapter :Source(),TargetTable {
    //只需要实现自己的新方法，无需处理原类中的method
    override fun method1() {
        println("我是adapter中的method1")
    }
}

//对象的适配器模式
class Wrapper:TargetTable{
    private lateinit var mSource:Source

    constructor(source: Source):super(){
//    constructor(source: Source){
        mSource=source
    }

    override fun method() {
        mSource?.method()
    }

    override fun method1() {
        println("我是wrapper中的method1")
    }

}

//接口的适配器是这样的：有时我们写的一个接口中有多个抽象方法，当我们写该接口的实现类时，必须实现该接口的所有方法，这明显有时比较浪费，因为
//并不是所有的方法都是我们需要的，有时只需要某一些，此处为了解决这个问题， 我们引入了接口的适配器模式，借助于一个抽象类，该抽象类实现了该接口，实
//现了所有的方法，而我们不和原始的接口打交道，只和该抽象类取得联系，所以 我们写一个类，继承该抽象类，重写我们需要的方法就行
//比如 Android中 SimpleTextWatcher :TextWatcher

fun main() {
//    val adapter=Adapter()
//    adapter.method()
//    adapter.method1()

    val wrapper=Wrapper(Source())
    wrapper.method()
    wrapper.method1()
}