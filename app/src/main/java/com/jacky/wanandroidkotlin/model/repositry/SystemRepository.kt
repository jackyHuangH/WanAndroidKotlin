package com.jacky.wanandroidkotlin.model.repositry

import com.jacky.wanandroidkotlin.model.api.WanResponse
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity

/**
 * @author:Hzj
 * @date  :2019/7/5/005
 * desc  ：体系 数据仓库
 * record：
 */
class SystemRepository : CollectRepository() {

    /**
     * 获取体系树 列表
     */
    suspend fun getSystemTreeList(): WanResponse<List<TreeParentEntity>> {
        return apiCall { WanRetrofitClient.mService.getSystemTreeList() }
    }

    /**
     * 根据子id获取体系文章列表
     */
    suspend fun getSystemArticleList(page: Int, cid: Int): WanResponse<ArticleList> {
        return apiCall { WanRetrofitClient.mService.getSystemArticleListByCid(page, cid) }
    }

    /**
     * 根据公众号id分页获取公众号文章列表
     */
    suspend fun getBlogListWithId(page: Int, blogId: Int): WanResponse<ArticleList> {
        return apiCall { WanRetrofitClient.mService.getBlogArticleList(page, blogId) }
    }
}