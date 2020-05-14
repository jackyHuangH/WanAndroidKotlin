package com.jacky.wanandroidkotlin.ui.search

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.base.executeRequest
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.HotEntity
import com.jacky.wanandroidkotlin.model.repositry.SearchRepository

/**
 * @author:Hzj
 * @date  :2019-07-20
 * desc  ：搜索ViewModel
 * record：
 */
class SearchViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository by lazy { SearchRepository() }
    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()
    val mCommonWebsiteData: MutableLiveData<List<HotEntity>> = MutableLiveData()
    val mHotKeyData: MutableLiveData<List<HotEntity>> = MutableLiveData()

    fun getHotKeys() {
        executeRequest(request = { mRepository.getSearchHotKeys() },
            onNext = { ok, data, msg ->
                if (ok) {
                    mHotKeyData.value = data
                }
            })
    }

    fun getCommonWebsites() {
        executeRequest(request = { mRepository.getCommonWebsites() },
            onNext = { ok, data, msg ->
                if (ok) {
                    mCommonWebsiteData.value = data
                }
            })
    }

    fun searchWithKeyword(pageNum: Int, keyword: String) {
        executeRequest(request = { mRepository.searchWithKeyword(pageNum, keyword) },
            onNext = { ok, data, msg ->
                if (ok) {
                    mArticleList.value = data
                }
            })
    }

    fun collectArticle(articleId: Int, boolean: Boolean) {
        executeRequest(request = {
            if (boolean)
                mRepository.collectArticle(articleId)
            else
                mRepository.unCollectArticle(articleId)
        })
    }
}