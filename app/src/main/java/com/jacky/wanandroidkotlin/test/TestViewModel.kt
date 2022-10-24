package com.jacky.wanandroidkotlin.test

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.jacky.support.utils.LoggerKit
import com.jacky.support.utils.TimeUtils
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.api.dispatch
import com.jacky.wanandroidkotlin.model.entity.TodayInHistoryEntity
import com.jacky.wanandroidkotlin.wrapper.orNotNullNotEmpty
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

/**
 * 测试 flow +LiveData
 */
class TestViewModel(application: Application) : BaseViewModel(application) {
    //可变的MutableLiveData私有，不对外暴露
    private val _todayInHistoryData: MutableLiveData<List<TodayInHistoryEntity>> = MutableLiveData()

    //对外暴露不可变的LiveData，只读
    val mTodayInHistoryData: LiveData<List<TodayInHistoryEntity>> = _todayInHistoryData


    /**
     * 获取历史上的今天
     *
     */
    fun getTodayInHistory() {
        launchOnUI {
            flow {
                val dateString = TimeUtils.getTimeDate2(System.currentTimeMillis())
                //使用flow处理
                val result = WanRetrofitClient.mService.getJuHeTodayInHistory(dateString)
                emit(result)
            }
                .onStart {
                    // 在调用 flow 请求数据之前，做一些准备工作，例如显示正在加载数据的进度条
                }.catch { throwable ->
                    //处理异常
                    throwable.dispatch(msgResult = { msg ->
                        mErrorMsg.value = msg
                    }, apiRefused = {})
                    _todayInHistoryData.value = null
                }.onCompletion { c ->
                    //请求完成
                    LoggerKit.d("today complete:${c?.message.orNotNullNotEmpty("无异常")}")
                }.collect { data ->
                    if (data != null && data.error_code == 0) {
                        // 将数据提供给 Activity 或者 Fragment
                        _todayInHistoryData.value = data.result
                    }
                }
        }
    }

    /**
     * 获取历史上的今天，方法2，使用asLiveData()扩展函数，返回liveData
     */
    fun getTodayInHistory2() = flow {
        val dateString = TimeUtils.getTimeDate2(System.currentTimeMillis())
        //使用flow处理
        val result = WanRetrofitClient.mService.getJuHeTodayInHistory(dateString)
        emit(result)
    }.onStart {
        // 在调用 flow 请求数据之前，做一些准备工作，例如显示正在加载数据的进度条
        mShowLoadingProgress.value=true
    }.catch { throwable ->
        //处理异常
        throwable.dispatch(msgResult = { msg ->
            mErrorMsg.value = msg
        }, apiRefused = {})
    }.onCompletion {
        //请求完成
        mShowLoadingProgress.value=false
    }.asLiveData()

}