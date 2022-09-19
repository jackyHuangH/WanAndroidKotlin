package com.jacky.wanandroidkotlin.model.api

import com.google.gson.JsonArray
import com.jacky.wanandroidkotlin.model.entity.*
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
     * 首页置顶文章列表
     */
    @GET("/article/top/json")
    suspend fun getHomeTopArticles(): WanResponse<List<ArticleEntity>>

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
    suspend fun getProjectListWithTypeId(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): WanResponse<ArticleList>

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
    suspend fun getBlogArticleList(
        @Path("page") page: Int,
        @Path("id") id: Int
    ): WanResponse<ArticleList>

    /**
     * 获取体系分类树列表
     */
    @GET("/tree/json")
    suspend fun getSystemTreeList(): WanResponse<MutableList<TreeParentEntity>>

    /**
     * 根据体系子id分页获取文章列表
     */
    @GET("/article/list/{page}/json")
    suspend fun getSystemArticleListByCid(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): WanResponse<ArticleList>

    /**
     * 获取导航列表
     */
    @GET("/navi/json")
    suspend fun getNavigation(): WanResponse<MutableList<NavigationEntity>>

    /**
     * 获取我的收藏文章列表
     */
    @GET("/lg/collect/list/{page}/json")
    suspend fun getMyCollectArticleList(@Path("page") page: Int): WanResponse<ArticleList>

    /**
     * 获取常用网站
     */
    @GET("/friend/json")
    suspend fun getCommonWebsites(): WanResponse<List<HotEntity>>

    /**
     * 获取搜索热词
     */
    @GET("/hotkey/json")
    suspend fun getSearchHotKeys(): WanResponse<List<HotEntity>>

    /**
     * 搜索关键字
     */
    @FormUrlEncoded
    @POST("/article/query/{page}/json")
    suspend fun searchHot(
        @Path("page") page: Int,
        @Field("k") key: String
    ): WanResponse<ArticleList>


    /**
     * Google Maven仓库快速查询
     */
    @GET("/maven_pom/search/json")
    suspend fun searchGoogleMavenPom(@Query("k") key: String): WanResponse<MutableList<GoogleMavenEntity>>

    /**
     * 干货提供的福利图片api
     */
    @GET("http://shibe.online/api/{keyword}")
    suspend fun getGirlsList(
        @Path("keyword") keyword: String,
        @Query("count") pageSize: Int = 10
    ): JsonArray?
}