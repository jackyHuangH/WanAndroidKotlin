package com.jacky.wanandroidkotlin.test

import kotlinx.coroutines.*

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
        //java 调用时，jvm会转成静态方法
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


/**
 * 作   者： by Hzj on 2018/1/4/004.
 * 描   述：Koltin 中的类可以有一个 主构造器，以及一个或多个次构造器，主构造器是类头部的一部分，位于类名称之后:
class Person constructor(firstName: String) {
}
如果主构造器没有任何注解，也没有任何可见度修饰符，那么constructor关键字可以省略。

class Person(firstName: String) {
}
 * 修订记录：
 */
open class Person constructor(age: Int) {

    //构造里的初始化方法，相当于Java的static{}代码块
    init {
        println("初始化:age=$age")
    }

    //次构造函数
    constructor (age: Int, name: String) : this(age) {
        this.name = name
        println("初始化:name=$name")
    }


    var name: String? = "z"
        get() = "姓名:$field"
        set(value) {
            //如果可空类型变量为null时，返回null
            field = value?.toUpperCase()
        }

    var age: Int = age
        get() = field
        set(value) {
            field = if (value > 30) 30 else value
        }


    //kotlin函数写法
    fun eat(food: String, water: String): String {
        return food + water
    }

    fun add(x: Int, y: Int) = x + y

    /**
     * 可变参数写法，用vararg 修饰参数
     */
    fun add(vararg v: Int) {
        var temp: Int = 0
        for (vt in v) {
            temp += vt
            println(temp)
        }
    }

}

//-----------------------扩展函数，扩展属性---------------------
fun String.method1(i: Int) {

}

val a: String.(Int) -> Unit = String::method1
val b: (String, Int) -> Unit = String::method1
val c: (String, Int) -> Unit = a
val d: String.(Int) -> Unit = b


