package com.jacky.wanandroidkotlin.util

import androidx.annotation.IntRange
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*

/**
 * @author:Hzj
 * @date  :2020/6/16
 * desc  ：倒计时工具类
 * record：coroutineScope使用委托
 */
//使用协程实现倒计时
class CountDownClock private constructor(
    private val coroutineScope: CoroutineScope,
    @IntRange(from = 1) val maxCountDown: Long,
    private val observable: (Long) -> Unit
) : CoroutineScope by coroutineScope, DefaultLifecycleObserver {

    companion object {
        fun CoroutineScope.createCountDownClock(
            @IntRange(from = 1) maxCountDown: Long,
            lifecycle: Lifecycle? = null, observable: (Long) -> Unit
        ): CountDownClock {
            return CountDownClock(this, maxCountDown, observable).apply {
                lifecycle?.addObserver(this)
            }
        }
    }

    private var countDownJob: Job? = null

    private fun CoroutineScope.createJob(): Job = launch(Dispatchers.IO + SupervisorJob()) {
        var countDown = maxCountDown
        while (isActive && countDown >= 0) {
            launch(Dispatchers.Main) {
                //切换主线程更新ui
                observable.invoke(countDown)
            }
            delay(1000L)
            countDown--
        }
    }

    /**
     * 开始倒计时
     */
    fun start() {
        launch {
            countDownJob?.cancel()
            countDownJob = createJob()
            countDownJob?.join()
        }
    }

    /**
     * 停止倒计时
     */
    fun stop() {
        launch {
            launch(Dispatchers.Main) {
                if (countDownJob?.isCancelled == false) {
                    //切换主线程更新ui
                    observable.invoke(0L)
                }
            }
            countDownJob?.cancel()
            countDownJob = null
        }
    }

    //绑定生命周期自动取消任务
    override fun onDestroy(owner: LifecycleOwner) {
        countDownJob?.cancel()
        countDownJob = null
    }
}