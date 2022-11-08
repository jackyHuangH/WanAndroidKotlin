package com.jacky.wanandroidkotlin.ui.tabhome

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.base.executeRequest
import com.jacky.wanandroidkotlin.base.executeRequestAsync
import com.jacky.wanandroidkotlin.base.observe
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.api.dispatch
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.entity.BannerEntity
import com.jacky.wanandroidkotlin.model.entity.WanResponse
import com.jacky.wanandroidkotlin.model.repositry.HomeRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

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
        launchOnUI {
            var topArticleRequest: Deferred<WanResponse<List<ArticleEntity>>>? = null
            if (page == 0) {
                //获取置顶文章
                topArticleRequest = executeRequestAsync {
                    mRepository.getTopArticleList()
                }
            }
            val topArticleList = topArticleRequest?.await()?.data
            //分页获取文章列表
            executeRequestAsync { mRepository.getArticleList(page) }?.observe { ok, articleList, s ->
                if (ok) {
                    mArticleList.value = articleList?.apply {
                        if (page == 0) {
                            topArticleList?.let { topList ->
                                datas.addAll(0, topList)
                            }
                        }
                    }
                }
            }
        }
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

    /**
     * 获取当天天气
     */
    fun getWeatherNow() = flow {
        val location="120.2524,30.2554"
        val entity = WanRetrofitClient.mService.getTodayWeather(location)
        emit(entity)
    }.onStart {
        //
    }.onCompletion {

    }.catch { throwable ->
        //处理异常
        throwable.dispatch(msgResult = { msg ->
            mErrorMsg.value = msg
        })
    }.asLiveData()
}