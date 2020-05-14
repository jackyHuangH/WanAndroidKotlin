package com.jacky.wanandroidkotlin.ui.project

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.base.executeRequest
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.model.repositry.ProjectRepository

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：
 * record：
 */
class ProjectViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository by lazy { ProjectRepository() }
    val mTabList: MutableLiveData<List<TreeParentEntity>> = MutableLiveData()
    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()

    fun getProjectType() {
        executeRequest(request = { mRepository.getProjectType() },
            onNext = { ok, data, msg ->
                if (ok) {
                    mTabList.value = data
                }
            })
    }

    fun getProjectList(page: Int, cid: Int) {
        executeRequest(request = { mRepository.getProjectListWithTypeId(page, cid) },
            onNext = { ok, data, msg ->
                if (ok) {
                    mArticleList.value = data
                }
            })
    }

    fun getBlogType() {
        executeRequest(request = { mRepository.getBlogType() },
            onNext = { ok, data, msg ->
                if (ok) {
                    mTabList.value = data
                }
            })
    }

    fun getBlogList(page: Int, blogId: Int) {
        executeRequest(request = { mRepository.getBlogListWithId(page, blogId) },
            onNext = { ok, data, msg ->
                if (ok) {
                    mArticleList.value = data
                }
            })
    }

    fun getLastedProjectList(page: Int) {
        executeRequest(request = { mRepository.getLatestProjectList(page) },
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