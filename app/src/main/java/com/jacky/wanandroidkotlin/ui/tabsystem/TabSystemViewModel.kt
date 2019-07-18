package com.jacky.wanandroidkotlin.ui.tabsystem

import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.model.repositry.SystemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author:Hzj
 * @date  :2019/7/5/005
 * desc  ：
 * record：
 */
class TabSystemViewModel : BaseViewModel() {
    private val mRepository by lazy { SystemRepository() }
    val mTreeList: MutableLiveData<List<TreeParentEntity>> = MutableLiveData()
    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()

    //获取体系树列表
    fun getSystemTreeList() {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.getSystemTreeList() }
            executeResponse(response, { mTreeList.value = response.data }, { mErrorMsg.value = response.errorMsg })
        }
    }

    fun getSystemArticleListByCid(page: Int, cid: Int) {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.getSystemArticleList(page, cid) }
            executeResponse(
                response,
                { mArticleList.value = response.data },
                { mErrorMsg.value = response.errorMsg })
        }
    }

    /**
     * 获取公众号文章列表
     */
    fun getBlogList(page: Int, blogId: Int) {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.getBlogListWithId(page, blogId) }
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