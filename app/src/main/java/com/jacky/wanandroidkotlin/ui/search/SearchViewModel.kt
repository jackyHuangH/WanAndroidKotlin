package com.jacky.wanandroidkotlin.ui.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.base.executeRequest
import com.jacky.wanandroidkotlin.base.executeRequestAsync
import com.jacky.wanandroidkotlin.base.observe
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

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        getCommonWebsitesAndHotKeysAsync()
    }

    //异步获取两个接口数据
    private fun getCommonWebsitesAndHotKeysAsync() {
        launchOnUI {
            val commonWebsitesDeferred = executeRequestAsync(request = {
                mRepository.getCommonWebsites()
            })
            val hotKeysDeferred = executeRequestAsync(request = {
                mRepository.getSearchHotKeys()
            })
            commonWebsitesDeferred.observe { ok, list, s ->
                if (ok) {
                    mCommonWebsiteData.value = list
                }
            }
            hotKeysDeferred.observe { ok, list, s ->
                if (ok) {
                    mHotKeyData.value = list
                }
            }
        }
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