package com.jacky.wanandroidkotlin.base

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacky.wanandroidkotlin.model.api.WanResponse
import kotlinx.coroutines.*


/**
 * @author:Hzj
 * @date  :2019/6/24/024
 * desc  ：ViewModel基类
 * record：<p>suspend，用作修饰会被暂停的函数，被标记为 suspend 的函数只能运行在协程或者其他 suspend 函数当中。</p>
 */
open class BaseViewModel : ViewModel(), LifecycleObserver {
    private val mException: MutableLiveData<Exception> = MutableLiveData()
    //错误信息
    val mErrorMsg: MutableLiveData<String> = MutableLiveData()

    companion object {
        const val ERROR_CODE = -1//请求失败返回码
    }

    private fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            Log.d("Thread", "launchOnUI:${Thread.currentThread().name}")
            block()
        }
    }

    fun launch(tryBlock: suspend CoroutineScope.() -> Unit) {
        Log.d("Thread", "launch:${Thread.currentThread().name}")
        launchOnUI {
            tryCatch(tryBlock, {}, {}, true)
        }
    }

    private suspend fun tryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
        finallyBlock: suspend CoroutineScope.() -> Unit,
        handleCancellationExceptionManually: Boolean = false
    ) {
        coroutineScope {
            Log.d("Thread", "tryCatch:${Thread.currentThread().name}")
            try {
                tryBlock()
            } catch (e: Exception) {
                if (e !is CancellationException || handleCancellationExceptionManually) {
                    mException.value = e
                    catchBlock(e)
                } else {
                    throw e
                }
            } finally {
                finallyBlock()
            }
        }
    }

    suspend fun executeResponse(
        response: WanResponse<Any>,
        successBlock: suspend CoroutineScope.() -> Unit,
        errorBlock: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            Log.d("Thread", "executeResponse:${Thread.currentThread().name}")
            if (response.errorCode == ERROR_CODE) errorBlock()
            else successBlock()
        }
    }

    fun launchOnUITryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
        finallyBlock: suspend CoroutineScope.() -> Unit,
        handleCancellationExceptionManually: Boolean
    ) {
        launchOnUI {
            tryCatch(tryBlock, catchBlock, finallyBlock, handleCancellationExceptionManually)
        }
    }

    fun launchOnUITryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        handleCancellationExceptionManually: Boolean = false
    ) {
        launchOnUI {
            tryCatch(tryBlock, {}, {}, handleCancellationExceptionManually)
        }
    }

    suspend fun <T> launchIO(block: suspend CoroutineScope.() -> T) {
        withContext(Dispatchers.IO) {
            block()
        }
    }
}