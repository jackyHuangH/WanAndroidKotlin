package com.jacky.wanandroidkotlin.ui.tabsystem

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.base.executeRequest
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.model.repositry.SystemRepository

/**
 * @author:Hzj
 * @date  :2019/7/5/005
 * desc  ：
 * record：
 */
class TabSystemViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository by lazy { SystemRepository() }
    val mTreeList: MutableLiveData<List<TreeParentEntity>> = MutableLiveData()
    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()

    //获取体系树列表
    fun getSystemTreeList() {
        executeRequest(request = { mRepository.getSystemTreeList() },
            onNext = { ok, data, msg ->
                if (ok) {
                    mTreeList.value = data
                }
            })
    }

    fun getSystemArticleListByCid(page: Int, cid: Int) {
        executeRequest(request = { mRepository.getSystemArticleList(page, cid) },
            onNext = { ok, data, msg ->
                if (ok) {
                    mArticleList.value = data
                }
            })
    }

    /**
     * 获取公众号文章列表
     */
    fun getBlogList(page: Int, blogId: Int) {
        executeRequest(request = { mRepository.getBlogListWithId(page, blogId) },
            onNext = { ok, data, msg ->
                if (ok) {
                    mArticleList.value = data
                }
            })
    }

    fun collectArticle(articleId: Int, collect: Boolean) {
        executeRequest(request = {
            if (collect) {
                mRepository.collectArticle(articleId)
            } else {
                mRepository.unCollectArticle(articleId)
            }
        })
    }
}