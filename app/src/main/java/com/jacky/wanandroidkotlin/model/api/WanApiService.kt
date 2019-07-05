package com.jacky.wanandroidkotlin.model.api

import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.BannerEntity
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.model.entity.UserEntity
import retrofit2.http.*

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

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(
        @Field("username") userName: String,
        @Field("password") passWord: String
    ): WanResponse<UserEntity>

    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("/user/register")
    suspend fun register(
        @Field("username") userName: String,
        @Field("password") passWord: String,
        @Field("repassword") rePassWord: String
    ): WanResponse<UserEntity>

    /**
     * 退出登录
     */
    @GET("/user/logout/json")
    suspend fun logout(): WanResponse<Any>

    /**
     * 首页文章列表分页获取
     */
    @GET("/article/list/{page}/json")
    suspend fun getHomeArticles(@Path("page") page: Int): WanResponse<ArticleList>

    /**
     * 首页banner
     */
    @GET("/banner/json")
    suspend fun getHomeBanner(): WanResponse<List<BannerEntity>>

    /**
     * 收藏站内文章
     */
    @POST("/lg/collect/{id}/json")
    suspend fun collectArticle(@Path("id") id: Int): WanResponse<ArticleList>

    /**
     * 取消收藏站内文章
     */
    @POST("/lg/uncollect_originId/{id}/json")
    suspend fun cancelCollectArticle(@Path("id") id: Int): WanResponse<ArticleList>

    /**
     * 项目分类tab
     */
    @GET("/project/tree/json")
    suspend fun getProjectType(): WanResponse<List<TreeParentEntity>>

    /**
     * 项目按类型分页获取列表
     */
    @GET("/project/list/{page}/json")
    suspend fun getProjectListWithTypeId(@Path("page") page: Int, @Query("cid") cid: Int): WanResponse<ArticleList>

    /**
     * 分页获取最新的项目列表
     */
    @GET("/article/listproject/{page}/json")
    suspend fun getLastedProjectList(@Path("page") page: Int): WanResponse<ArticleList>

    /**
     * 获取公众号分类tab
     */
    @GET("/wxarticle/chapters/json")
    suspend fun getBlogType(): WanResponse<List<TreeParentEntity>>

    /**
     * 根据公众号id分页获取公众号文章列表
     */
    @GET("/wxarticle/list/{id}/{page}/json")
    suspend fun getBlogArticleList(@Path("page") page: Int, @Path("id") id: Int): WanResponse<ArticleList>

    /**
     * 获取体系分类树列表
     */
    @GET("/tree/json")
    suspend fun getSystemTreeList(): WanResponse<List<TreeParentEntity>>

    /**
     * 根据体系子id分页获取文章列表
     */
    @GET("/article/list/{page}/json")
    suspend fun getSystemArticleListByCid(@Path("page") page: Int, @Query("cid") cid: Int): WanResponse<ArticleList>
}