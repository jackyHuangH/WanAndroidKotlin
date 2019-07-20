package com.jacky.wanandroidkotlin.ui.mycollect

import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.repositry.MyCollectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author:Hzj
 * @date  :2019-07-20
 * desc  ：我的收藏ViewModel
 * record：
 */
class MyCollectViewModel : BaseViewModel() {
    private val mRepository by lazy { MyCollectRepository() }
    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()

    fun getMyCollectArticleList(pageNum: Int) {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.getMyCollectArticleList(pageNum) }
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