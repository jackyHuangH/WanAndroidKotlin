package com.jacky.wanandroidkotlin.model.repositry

import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.UserEntity
import com.jacky.wanandroidkotlin.model.entity.WanResponse

/**
 * @author:Hzj
 * @date  :2019/7/3/003
 * desc  ：用户数据仓库
 * record：
 */
class UserRepository {

    /**
     * 登录
     */
    suspend fun login(name: String, password: String): WanResponse<UserEntity> {
        return WanRetrofitClient.mService.login(name, password)
    }

    /**
     * 注册
     */
    suspend fun register(
        name: String,
        password: String,
        rePassword: String
    ): WanResponse<UserEntity> {
        return WanRetrofitClient.mService.register(name, password, rePassword)
    }

    /**
     * 退出登录，要清除cookie和用户信息本地缓存
     */
    suspend fun logout(): WanResponse<Any> {
        return WanRetrofitClient.mService.logout()
    }
}