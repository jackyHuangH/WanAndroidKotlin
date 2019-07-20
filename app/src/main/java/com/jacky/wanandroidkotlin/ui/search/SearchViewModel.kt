package com.jacky.wanandroidkotlin.ui.search

import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.HotEntity
import com.jacky.wanandroidkotlin.model.repositry.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author:Hzj
 * @date  :2019-07-20
 * desc  ：搜索ViewModel
 * record：
 */
class SearchViewModel : BaseViewModel() {
    private val mRepository by lazy { SearchRepository() }
    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()
    val mCommonWebsiteData: MutableLiveData<List<HotEntity>> = MutableLiveData()
    val mHotKeyData: MutableLiveData<List<HotEntity>> = MutableLiveData()

    fun getHotKeys() {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.getSearchHotKeys() }
            executeResponse(response,
                { mHotKeyData.value = response.data },
                { mErrorMsg.value = response.errorMsg })
        }
    }

    fun getCommonWebsites() {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.getCommonWebsites() }
            executeResponse(response,
                { mCommonWebsiteData.value = response.data },
                { mErrorMsg.value = response.errorMsg })
        }
    }

    fun searchWithKeyword(pageNum: Int, keyword: String) {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.searchWithKeyword(pageNum, keyword) }
            executeResponse(response,
                { mArticleList.value = response.data },
                { mErrorMsg.value = response.errorMsg })
        }
    }

    fun collectArticle(articleId: Int, boolean: Boolean) {
        launch {
            val result = withContext(Dispatchers.IO) {
                if (boolean) mRepository.collectArticle(articleId)
                else mRepository.unCollectArticle(articleId)
            }
        }
    }
}