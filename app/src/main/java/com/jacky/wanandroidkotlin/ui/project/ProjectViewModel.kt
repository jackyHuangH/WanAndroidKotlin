package com.jacky.wanandroidkotlin.ui.project

import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.model.repositry.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：
 * record：
 */
class ProjectViewModel : BaseViewModel() {
    private val mRepository by lazy { ProjectRepository() }
    val mTabList: MutableLiveData<List<TreeParentEntity>> = MutableLiveData()
    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()

    fun getProjectType() {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.getProjectType() }
            executeResponse(response, { mTabList.value = response.data }, { mErrorMsg.value = response.errorMsg })
        }
    }

    fun getProjectList(page: Int, cid: Int) {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.getProjectListWithTypeId(page, cid) }
            executeResponse(response, { mArticleList.value = response.data }, { mErrorMsg.value = response.errorMsg })
        }
    }

    fun getBlogType() {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.getBlogType() }
            executeResponse(response, { mTabList.value = response.data }, { mErrorMsg.value = response.errorMsg })
        }
    }

    fun getBlogList(page: Int, blogId: Int) {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.getBlogListWithId(page, blogId) }
            executeResponse(response, { mArticleList.value = response.data }, { mErrorMsg.value = response.errorMsg })
        }
    }

    fun getLastedProjectList(page: Int) {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.getLatestProjectList(page) }
            executeResponse(response, { mArticleList.value = response.data }, { mErrorMsg.value = response.errorMsg })
        }
    }

    fun collectArticle(articleId: Int, collect: Boolean) {
        launch {
            withContext(Dispatchers.IO) {
                if (collect) {
                    mRepository.collectArticle(articleId)
                } else {
                    mRepository.unCollectArticle(articleId)
                }
            }
        }
    }
}