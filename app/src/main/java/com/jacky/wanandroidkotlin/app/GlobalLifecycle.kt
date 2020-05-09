package com.jacky.wanandroidkotlin.app

import android.app.Activity
import android.os.Process
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.jacky.wanandroidkotlin.ui.splash.navigateToRestart
import java.lang.ref.WeakReference
import java.util.*

/**
 * @author:Hzj
 * @date  :2020/5/7
 * desc  ： 全局生命周期观察者
 * record：
 */

class GlobalLifecycleObserver : DefaultLifecycleObserver {
    //activity 引用任务栈
    private val actStack = Stack<WeakReference<Activity>>()

    //记录所有的callback
    private val callbackStack = Stack<WeakReference<ActivityLifecycleCallback>>()

    //记录前台activity数目
    private var foregroundActCount = 0

    override fun onCreate(owner: LifecycleOwner) {
        (owner as Activity).let {
            actStack.add(WeakReference(it))
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        if (foregroundActCount == 0) {
            for (index in callbackStack.indices.reversed()) {
                callbackStack[index].get()?.onForeground()
            }
        }
        foregroundActCount++
    }

    override fun onStop(owner: LifecycleOwner) {
        foregroundActCount--
        if (foregroundActCount == 0) {
            for (i in callbackStack.indices.reversed()) {
                callbackStack[i].get()?.onBackground()
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        for (index in actStack.indices) {
            if (actStack[index].get() == owner) {
                actStack.removeAt(index)
                break
            }
        }
    }

    fun clearAll() {
        actStack.clear()
        callbackStack.clear()
    }

    fun getTopActivity(): Activity? = if (actStack.empty()) null else actStack.peek().get()

    fun finishAllActivity() {
        for (weakReference in actStack) {
            weakReference.get()?.finish()
        }
    }

    fun exitApp() {
        for (i in callbackStack.indices.reversed()) {
            callbackStack[i].get()?.onDestroyedSelf()
        }
        finishAllActivity()
        clearAll()
        Process.killProcess(Process.myPid())
    }

    fun addActivityCycleCallback(callback: ActivityLifecycleCallback) {
        for (i in callbackStack.indices.reversed()) {
            if (callbackStack[i].get() == callback) {
                return
            }
        }
        callbackStack.add(WeakReference(callback))
    }

    fun removeActivityCycleCallback(callback: ActivityLifecycleCallback) {
        for (i in callbackStack.indices.reversed()) {
            if (callbackStack[i].get() == callback) {
                callbackStack.removeAt(i)
                return
            }
        }
    }

    companion object {
        val INSTANCE = GlobalLifecycleObserver()

        fun restartApp(gotoLogin: Boolean = false) {
            INSTANCE.getTopActivity().navigateToRestart(gotoLogin)
        }
    }
}

interface ActivityLifecycleCallback {
    fun onBackground()
    fun onForeground()
    fun onDestroyedSelf()
}