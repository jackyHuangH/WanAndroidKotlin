package com.jacky.wanandroidkotlin.model.repositry

import com.jacky.wanandroidkotlin.model.api.BaseRepository
import com.jacky.wanandroidkotlin.model.api.WanResponse
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.ProjectTypeParent

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：项目 数据仓库
 * record：
 */
class ProjectRepository : BaseRepository() {
    /**
     * 获取项目分类tab
     */
    suspend fun getProjectType(): WanResponse<List<ProjectTypeParent>> {
        return apiCall { WanRetrofitClient.mService.getProjectType() }
    }

    /**
     * 根据类型id分页获取项目列表
     */
    suspend fun getProjectListWithTypeId(page: Int, typeId: Int): WanResponse<ArticleList> {
        return apiCall { WanRetrofitClient.mService.getProjectListWithTypeId(page, typeId) }
    }

    /**
     * 分页获取最新的项目列表
     */
    suspend fun getLatestProjectList(page: Int): WanResponse<ArticleList> {
        return apiCall { WanRetrofitClient.mService.getLastedProjectList(page) }
    }

    /**
     * 获取公众号分类tab
     */
    suspend fun getBlogType(): WanResponse<List<ProjectTypeParent>> {
        return apiCall { WanRetrofitClient.mService.getBlogType() }
    }

    /**
     * 根据公众号id分页获取公众号文章列表
     */
    suspend fun getBlogListWithId(page: Int, blogId: Int): WanResponse<ArticleList> {
        return apiCall { WanRetrofitClient.mService.getBlogArticleList(page, blogId) }
    }

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