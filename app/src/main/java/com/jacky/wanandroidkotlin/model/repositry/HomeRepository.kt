package com.jacky.wanandroidkotlin.model.repositry

import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.BannerEntity
import com.jacky.wanandroidkotlin.model.entity.WanResponse

/**
 * @author:Hzj
 * @date  :2019/7/2/002
 * desc  ：首页获取数据 model管理仓库
 * record：
 */
class HomeRepository : CollectRepository() {

    /**
     * 获取首页置顶文章列表
     */
    suspend fun getTopArticleList(): WanResponse<List<ArticleEntity>> {
        return WanRetrofitClient.mService.getHomeTopArticles()
    }

    /**
     * 分页获取首页文章列表
     */
    suspend fun getArticleList(page: Int): WanResponse<ArticleList> {
        return WanRetrofitClient.mService.getHomeArticles(page)
    }

    /**
     * 获取首页banner数据
     */
    suspend fun getHomeBanner(): WanResponse<List<BannerEntity>> {
        return WanRetrofitClient.mService.getHomeBanner()
    }
}