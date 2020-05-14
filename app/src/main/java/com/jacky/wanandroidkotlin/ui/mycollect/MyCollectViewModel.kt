package com.jacky.wanandroidkotlin.ui.mycollect

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.base.executeRequest
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.repositry.MyCollectRepository

/**
 * @author:Hzj
 * @date  :2019-07-20
 * desc  ：我的收藏ViewModel
 * record：
 */
class MyCollectViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository by lazy { MyCollectRepository() }
    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()

    fun getMyCollectArticleList(pageNum: Int) {
        executeRequest(request = { mRepository.getMyCollectArticleList(pageNum) },
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