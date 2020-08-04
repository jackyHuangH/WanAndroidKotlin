package com.jacky.wanandroidkotlin.ui.search

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.model.entity.HotEntity
import com.jacky.wanandroidkotlin.ui.adapter.HomeListAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.RecyclerViewHelper
import com.jakewharton.rxbinding2.widget.RxTextView
import com.zenchn.support.router.Router
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.widget.VerticalItemDecoration
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_search.*

/**
 * 搜索
 */
class SearchActivity : BaseVMActivity<SearchViewModel>(), BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.RequestLoadMoreListener, BaseQuickAdapter.OnItemChildClickListener {

    private var mPageNum = 0
    private var mKeyword = ""
    private val mListAdapter by lazy { HomeListAdapter() }
    private val mCommonWebsiteList = mutableListOf<HotEntity>()
    private val mHotKeyList = mutableListOf<HotEntity>()
    private val mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)
    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun getLayoutId(): Int = R.layout.activity_search

    override fun initWidget() {
        ibt_back.setOnClickListener { onBackPressed() }
        initRecyclerView()
        initRefreshLayout()
        initTagFlowLayout()
        initEditText()
    }

    private fun initEditText() {
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

    private fun initTagFlowLayout() {
        web_tagLayout.apply {
            adapter = object : TagAdapter<HotEntity>(mCommonWebsiteList) {
                override fun getView(parent: FlowLayout?, position: Int, t: HotEntity?): View {
                    val tvTag =
                        LayoutInflater.from(parent?.context)
                            .inflate(R.layout.item_tag, parent, false) as TextView
                    tvTag.text = t?.name
                    return tvTag
                }
            }
            setOnTagClickListener { view, position, parent ->
                val url = mCommonWebsiteList[position].link
                BrowserActivity.launch(this@SearchActivity, url)
                true
            }
        }
        hot_tagLayout.apply {
            adapter = object : TagAdapter<HotEntity>(mHotKeyList) {
                override fun getView(parent: FlowLayout?, position: Int, t: HotEntity?): View {
                    val tvTag =
                        LayoutInflater.from(parent?.context)
                            .inflate(R.layout.item_tag, parent, false) as TextView
                    tvTag.text = t?.name
                    return tvTag
                }
            }
            setOnTagClickListener { view, position, parent ->
                mKeyword = mHotKeyList[position].name
                et_search.setText(mKeyword)
                doSearch()
                true
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
        if (mKeyword.isEmpty()) {
            showMessage("搜索关键字不能为空")
            return
        }
        //下拉刷新时禁用加载更多
        mListAdapter.setEnableLoadMore(false)
        swipe_refresh.isRefreshing = true
        mPageNum = 0
        mViewModel.searchWithKeyword(mPageNum, mKeyword)
    }

    private fun initRecyclerView() {
        rlv.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            setHasFixedSize(true)
            addItemDecoration(
                VerticalItemDecoration(
                    AndroidKit.Dimens.dp2px(10)
                )
            )
            adapter = mListAdapter.apply {
                onItemClickListener = this@SearchActivity
                onItemChildClickListener = this@SearchActivity
                emptyView = RecyclerViewHelper.getCommonEmptyView(rlv)
                setOnLoadMoreListener(this@SearchActivity, rlv)
                setLoadMoreView(CustomLoadMoreView())
            }
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        // 跳转详情
        adapter?.run {
            val entity = data[position] as ArticleEntity
            BrowserActivity.launch(this@SearchActivity, entity.link)
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        when (view?.id) {
            R.id.ibt_star -> {
                if (mIsLogin) {
                    //收藏
                    adapter?.run {
                        val entity = data[position] as ArticleEntity
                        entity.run {
                            collect = !collect
                            mViewModel.collectArticle(id, collect)
                        }
                        notifyDataSetChanged()
                    }
                } else {
                    //未登录，跳转登录
                    LoginActivity.launch(this@SearchActivity)
                }
            }
        }
    }

    override fun onLoadMoreRequested() {
        // 加载更多
        mPageNum++
        mViewModel.searchWithKeyword(mPageNum, mKeyword)
    }

    override val startObserve: SearchViewModel.() -> Unit = {
        mArticleList.observe(this@SearchActivity, Observer { articleList ->
            articleList?.let {
                swipe_refresh.visibility = View.VISIBLE
                scroll_view.visibility = View.GONE
                if (mPageNum == 0) {
                    mListAdapter.setNewData(it.datas)
                } else {
                    mListAdapter.addData(it.datas)
                }
                setLoadStatus(it.over.not())
            }
        })
        mCommonWebsiteData.observe(this@SearchActivity, Observer { list ->
            list?.let {
                mCommonWebsiteList.clear()
                mCommonWebsiteList.addAll(it)
                web_tagLayout.adapter.notifyDataChanged()
            }
        })
        mHotKeyData.observe(this@SearchActivity, Observer { list ->
            list?.let {
                mHotKeyList.clear()
                mHotKeyList.addAll(it)
                hot_tagLayout.adapter.notifyDataChanged()
            }
        })
        mErrorMsg.observe(this@SearchActivity, Observer {
            it?.let { onApiFailure(it) }
        })
    }

    private fun setLoadStatus(hasNextPage: Boolean) {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        mListAdapter.apply {
            if (hasNextPage) {
                loadMoreComplete()
            } else {
                loadMoreEnd()
            }
        }
    }

    override fun onApiFailure(msg: String) {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        super.onApiFailure(msg)
    }

    override fun onBackPressed() {
        if (swipe_refresh.visibility == View.VISIBLE) {
            swipe_refresh.visibility = View.GONE
            scroll_view.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        mCompositeDisposable.clear()
        super.onDestroy()
    }

    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(SearchActivity::class.java)
                .launch()
        }
    }
}
