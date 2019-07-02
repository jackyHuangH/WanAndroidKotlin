package com.jacky.wanandroidkotlin.model.api

/**
 * @author:Hzj
 * @date  :2019/7/2/002
 * desc  ：基础数据仓库
 * record：suspend () -> 挂起函数
 */
open class BaseRepository {

    suspend fun <T : Any> apiCall(call: suspend () -> WanResponse<T>): WanResponse<T> {
        return call.invoke()
    }
}