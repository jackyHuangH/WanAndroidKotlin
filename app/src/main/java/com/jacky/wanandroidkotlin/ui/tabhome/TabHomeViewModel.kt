package com.jacky.wanandroidkotlin.ui.tabhome

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.base.executeRequest
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.BannerEntity
import com.jacky.wanandroidkotlin.model.repositry.HomeRepository

/**
 * @author:Hzj
 * @date  :2019/7/2/002
 * desc  ：ViewModel,管理数据和展示，类似于MVP中的Presenter
 * record：
 */
class TabHomeViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository by lazy { HomeRepository() }
    val mBannerList: MutableLiveData<List<BannerEntity>> = MutableLiveData()
    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()

    //用于更新悬浮栏歌曲信息
    /**
     * 歌曲名
     */
    var name: ObservableField<String> = ObservableField()

    /**
     * 专辑封面id
     */
    var albumId: ObservableField<Long> = ObservableField(0)

    /**
     * 是否正在播放
     */
    var playStatusSelected: ObservableField<Boolean> = ObservableField(false)

    fun getBanners() {
        executeRequest(request = { mRepository.getHomeBanner() },
            onNext = { ok, data, msg ->
                if (ok) {
                    mBannerList.value = data
                }
            })
    }

    fun getArticleList(page: Int) {
        executeRequest(request = { mRepository.getArticleList(page) },
            onNext = { ok, data, msg ->
                mArticleList.value = data
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