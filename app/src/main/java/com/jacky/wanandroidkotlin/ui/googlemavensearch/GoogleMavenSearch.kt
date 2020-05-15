package com.jacky.wanandroidkotlin.ui.googlemavensearch

/**
 * @author:Hzj
 * @date  :2020/5/15
 * desc  ： Google Maven 快速查询
 * record：
 */

import android.app.Activity
import android.app.Application
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.base.executeRequest
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.GoogleMavenEntity
import com.jacky.wanandroidkotlin.ui.adapter.GoogleMavenSearchAdapter
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.wrapper.recyclerview.RecyclerViewHelper
import com.jakewharton.rxbinding2.widget.RxTextView
import com.zenchn.support.router.Router
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.widget.VerticalItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_google_maven_search.*


class GoogleMavenSearchActivity : BaseVMActivity<GoogleMavenSearchViewModel>() {

    private var mKeyword = ""
    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }
    private val mListAdapter by lazy { GoogleMavenSearchAdapter() }
    private var mLatestSearchWord by PreferenceUtil(PreferenceUtil.KEY_MAVEN_LATEST_SEARCH_WORD, "")

    override fun getLayoutRes(): Int = R.layout.activity_google_maven_search

    override fun initWidget() {
        ibt_back.setOnClickListener { onBackPressed() }
        initRecyclerView()
        initRefreshLayout()
        initEditText()
    }

    private fun initEditText() {
        et_search.setText(mLatestSearchWord)
        mCompositeDisposable.add(
            RxTextView
                .editorActions(et_search)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it == EditorInfo.IME_ACTION_SEARCH) {
                        mKeyword = et_search.text.toString()
                        doSearch()
                    }
                }
        )
    }

    private fun initRecyclerView() {
        rlv.apply {
            layoutManager = LinearLayoutManager(this@GoogleMavenSearchActivity)
            setHasFixedSize(true)
            addItemDecoration(
                VerticalItemDecoration(
                    AndroidKit.Dimens.dp2px(1)
                )
            )
            adapter = mListAdapter.apply {
                setOnItemChildClickListener { adapter, view, position ->
                    val item = adapter.getItem(position) as GoogleMavenEntity
                    if (view.id == R.id.tv_group_name) {
                        item.groupExpand = item.groupExpand.not()
                        notifyItemChanged(position)
                    }
                }
                emptyView = RecyclerViewHelper.getCommonEmptyView(rlv)
            }
        }
    }

    private fun initRefreshLayout() {
        swipe_refresh.apply {
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                //搜索内容
                doSearch()
            }
        }
    }

    private fun doSearch() {
        AndroidKit.Keyboard.hideSoftInput(this)
        swipe_refresh.isRefreshing = true
        mViewModel.searchGoogleMavenPom(mKeyword)
    }

    override val startObserve: GoogleMavenSearchViewModel.() -> Unit = {
        mGooglePomData.observe(this@GoogleMavenSearchActivity, Observer {
            swipe_refresh.isRefreshing = false
            mListAdapter.setNewData(it)
        })
        mSearchFailed.observe(this@GoogleMavenSearchActivity, Observer {
            swipe_refresh.isRefreshing = false
        })
    }

    override fun onPause() {
        mLatestSearchWord = et_search.text.toString()
        super.onPause()
    }

    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(GoogleMavenSearchActivity::class.java)
                .launch()
        }
    }

    override fun onApiFailure(msg: String) {
        swipe_refresh.isRefreshing = false
        super.onApiFailure(msg)
    }
}

class GoogleMavenSearchViewModel(application: Application) : BaseViewModel(application) {
    val mGooglePomData: MutableLiveData<List<GoogleMavenEntity>> = MutableLiveData()
    val mSearchFailed: MutableLiveData<Any> = MutableLiveData()

    fun searchGoogleMavenPom(key: String) {
        executeRequest(request = { WanRetrofitClient.mService.searchGoogleMavenPom(key) },
            onNext = { ok, data, msg ->
                if (ok) {
                    mGooglePomData.value = data
                } else {
                    mSearchFailed.value = 0
                }
            })
    }
}