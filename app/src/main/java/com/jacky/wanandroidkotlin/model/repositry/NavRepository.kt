package com.jacky.wanandroidkotlin.model.repositry

import com.jacky.wanandroidkotlin.model.api.BaseRepository
import com.jacky.wanandroidkotlin.model.api.WanResponse
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.NavigationEntity

/**
 * @author:Hzj
 * @date  :2019-07-18
 * desc  ：
 * record：
 */
class NavRepository:BaseRepository() {

    /**
     * 获取导航列表
     */
    suspend fun getNavigationList():WanResponse<List<NavigationEntity>>{
        return apiCall { WanRetrofitClient.mService.getNavigation() }
    }
}