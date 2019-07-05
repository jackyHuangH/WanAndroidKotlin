package com.jacky.wanandroidkotlin.model.repositry

import com.jacky.wanandroidkotlin.model.api.BaseRepository
import com.jacky.wanandroidkotlin.model.api.WanResponse
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.ArticleList

/**
 * @author:Hzj
 * @date  :2019/7/5/005
 * desc  ：收藏，取消收藏文章
 * record：
 */
open class CollectRepository : BaseRepository() {

    /**
     * 收藏文章
     */
    suspend fun collectArticle(articleId: Int): WanResponse<ArticleList> {
        return apiCall { WanRetrofitClient.mService.collectArticle(articleId) }
    }

    /**
     * 取消收藏文章
     */
    suspend fun unCollectArticle(articleId: Int): WanResponse<ArticleList> {
        return apiCall { WanRetrofitClient.mService.cancelCollectArticle(articleId) }
    }
}