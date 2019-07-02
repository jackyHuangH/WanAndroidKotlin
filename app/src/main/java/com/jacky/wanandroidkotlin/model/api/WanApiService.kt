package com.jacky.wanandroidkotlin.model.api

import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.BannerEntity
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author:Hzj
 * @date  :2019/7/2/002
 * desc  ：WanAndroid Api 接口
 * record：
 */
interface WanApiService {
    companion object {
        const val BASE_URL = "https://www.wanandroid.com"
    }

    @GET("/article/list/{page}/json")
    suspend fun getHomeArticles(@Path("page") page: Int): WanResponse<ArticleList>

    @GET("/banner/json")
    suspend fun getHomeBanner(): WanResponse<List<BannerEntity>>
}