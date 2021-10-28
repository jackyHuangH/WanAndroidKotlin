package com.jacky.wanandroidkotlin

import org.junit.Test

import org.junit.Assert.*
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val p=P()
        val referenceQueue=ReferenceQueue<P>()
        val weakReference=WeakReference<P>(p,referenceQueue)
        println(weakReference.get())
        System.gc()
        Thread.sleep(5000)
        println(weakReference.get())
        //若P被回收，则会存入referenceQueue中
        println(referenceQueue.poll())
    }
}

class P