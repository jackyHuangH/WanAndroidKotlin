package com.jacky.wanandroidkotlin.test

/**
 * @author:Hzj
 * @date :2018/10/24/024
 * desc  ：没有主构造函数
 * record：
 */
class Car {
    private var price: Int = 0

    var name: String? = null

    //这三个都是次构造函数
    constructor() {}

    constructor(price: Int) {
        this.price = price
    }

    constructor(price: Int, name: String) {
        this.price = price
        this.name = name
    }

    override fun toString(): String {
        return "name:${this.name},price:${this.price}"
    }

    //私有函数，返回类型是匿名对象类型
    private fun pFoo() = object {
        val x = "x"
    }

    private val aaa = object {
        val x = "x"
    }

    //object修饰类，对象声明
    object Mazda{

    }

    companion object {
        @JvmStatic fun callStatic() {
            println("callStatic")
        }
        fun callNonStatic() {
            println("callNonStatic")
        }
    }

    //公有函数，返回类型是Any
    fun publicFoo() = object {
        val y: String = "y"
    }

    fun go() {
        val x = pFoo().x
        val xx = aaa.x
        //无法解析引用 y
//        val y = publicFoo().y
    }
}
