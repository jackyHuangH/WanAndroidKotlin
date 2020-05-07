package com.jacky.wanandroidkotlin.model.api

import android.util.Log
import com.jacky.wanandroidkotlin.model.entity.WanResponse

/**
 * @author:Hzj
 * @date  :2019/7/2/002
 * desc  ：基础数据仓库
 * record：suspend () -> 挂起函数
 */
open class BaseRepository {

    suspend fun <T : Any> apiCall(call: suspend () -> WanResponse<T>): WanResponse<T> {
        Log.d("Thread", "apiCall:${Thread.currentThread().name}")
        return call.invoke()
    }
}