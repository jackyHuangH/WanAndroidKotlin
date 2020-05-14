package com.jacky.wanandroidkotlin.model.repositry

import com.jacky.wanandroidkotlin.model.entity.WanResponse
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.HotEntity

/**
 * @author:Hzj
 * @date  :2019-07-20
 * desc  ：搜索repository
 * record：
 */
class SearchRepository : CollectRepository() {

    /**
     * 获取常用网站
     */
    suspend fun getCommonWebsites(): WanResponse<List<HotEntity>> {
        return  WanRetrofitClient.mService.getCommonWebsites()
    }

    /**
     * 获取搜索热词
     */
    suspend fun getSearchHotKeys(): WanResponse<List<HotEntity>> {
        return  WanRetrofitClient.mService.getSearchHotKeys()
    }

    /**
     * 搜索关键字
     */
    suspend fun searchWithKeyword(pageNum: Int, word: String): WanResponse<ArticleList> {
        return WanRetrofitClient.mService.searchHot(pageNum, word)
    }
}