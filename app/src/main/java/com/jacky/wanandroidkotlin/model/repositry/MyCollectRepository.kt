package com.jacky.wanandroidkotlin.model.repositry

import com.jacky.wanandroidkotlin.model.entity.WanResponse
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.ArticleList

/**
 * @author:Hzj
 * @date  :2019-07-20
 * desc  ：我的收藏文章列表
 * record：
 */
class MyCollectRepository : CollectRepository() {

    /**
     * 获取我的收藏文章列表
     */
    suspend fun getMyCollectArticleList(pageNum: Int): WanResponse<ArticleList> {
        return apiCall { WanRetrofitClient.mService.getMyCollectArticleList(pageNum) }
    }

}