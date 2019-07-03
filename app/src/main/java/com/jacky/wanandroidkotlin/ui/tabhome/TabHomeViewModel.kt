package com.jacky.wanandroidkotlin.ui.tabhome

import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.BannerEntity
import com.jacky.wanandroidkotlin.model.repositry.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author:Hzj
 * @date  :2019/7/2/002
 * desc  ：ViewModel,管理数据和展示，类似于MVP中的Presenter
 * record：
 */
class TabHomeViewModel : BaseViewModel() {
    private val mRepository by lazy { HomeRepository() }
    val mBannerList: MutableLiveData<List<BannerEntity>> = MutableLiveData()
    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()

    fun getBanners() {
        launch {
            val result = withContext(Dispatchers.IO) { mRepository.getHomeBanner() }
            executeResponse(result, { mBannerList.value = result.data }, { mErrorMsg.value = result.errorMsg })
        }
    }

    fun getArticleList(page: Int) {
        launch {
            val result = withContext(Dispatchers.IO) { mRepository.getArticleList(page) }
            executeResponse(result, { mArticleList.value = result.data }, { mErrorMsg.value = result.errorMsg })
        }
    }

    fun collectArticle(articleId: Int, collect: Boolean) {
        launch {
            val result = withContext(Dispatchers.IO) {
                if (collect) {
                    mRepository.collectArticle(articleId)
                } else {
                    mRepository.unCollectArticle(articleId)
                }
            }
//            executeResponse(result, { mArticleList.value = result.data }, {})
        }
    }
}