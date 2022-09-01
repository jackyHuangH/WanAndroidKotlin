package com.jacky.wanandroidkotlin.ui.search

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.databinding.ActivitySearchBinding
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.model.entity.HotEntity
import com.jacky.wanandroidkotlin.ui.adapter.HomeListAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.RecyclerViewHelper
import com.jacky.wanandroidkotlin.wrapper.recyclerview.updateLoadMoreStatus
import com.jacky.wanandroidkotlin.wrapper.viewClickListener
import com.jacky.wanandroidkotlin.wrapper.viewVisibleExt
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jacky.support.router.Router
import com.jacky.support.utils.AndroidKit
import com.jacky.support.widget.VerticalItemDecoration
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * 搜索
 */
class SearchActivity : BaseVMActivity<ActivitySearchBinding, SearchViewModel>(),
    OnItemClickListener,
    OnLoadMoreListener, OnItemChildClickListener {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var etSearch: EditText
    private lateinit var webTaglayout: TagFlowLayout
    private lateinit var hotTaglayout: TagFlowLayout

    private var mPageNum = 0
    private var mKeyword = ""
    private val mListAdapter by lazy { HomeListAdapter() }
    private val mCommonWebsiteList = mutableListOf<HotEntity>()
    private val mHotKeyList = mutableListOf<HotEntity>()
    private val mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)
    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }


    override fun getLayoutId(): Int = R.layout.activity_search

    override fun initWidget() {
        viewClickListener(R.id.ibt_back) { onBackPressed() }
        swipeRefreshLayout = getView<SwipeRefreshLayout>(R.id.swipe_refresh)
        etSearch = getView<EditText>(R.id.et_search)
        webTaglayout = getView<TagFlowLayout>(R.id.web_tagLayout)
        hotTaglayout = getView<TagFlowLayout>(R.id.hot_tagLayout)
        initRecyclerView()
        initRefreshLayout()
        initTagFlowLayout()
        initEditText()
    }

    private fun initEditText() {
        mCompositeDisposable.add(
            RxTextView
                .editorActions(etSearch)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it == EditorInfo.IME_ACTION_SEARCH) {
                        mKeyword = etSearch.text.toString()
                        doSearch()
                    }
                }
        )
    }

    private fun initTagFlowLayout() {
        webTaglayout.apply {
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
        hotTaglayout.apply {
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
                etSearch.setText(mKeyword)
                doSearch()
                true
            }
        }
    }

    private fun initRefreshLayout() {
        swipeRefreshLayout.apply {
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
        mListAdapter.loadMoreModule.isEnableLoadMore = false
        swipeRefreshLayout.isRefreshing = true
        mPageNum = 0
        mViewModel.searchWithKeyword(mPageNum, mKeyword)
    }

    private fun initRecyclerView() {
        val rlv = getView<RecyclerView>(R.id.rlv)
        rlv.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            setHasFixedSize(true)
            if (itemDecorationCount <= 0) {
                addItemDecoration(
                    VerticalItemDecoration(
                        AndroidKit.Dimens.dp2px(10)
                    )
                )
            }
            adapter = mListAdapter.apply {
                setOnItemClickListener(this@SearchActivity)
                addChildClickViewIds(R.id.ibt_star)
                setOnItemChildClickListener(this@SearchActivity)
                setEmptyView(RecyclerViewHelper.getCommonEmptyView(rlv))
                loadMoreModule.setOnLoadMoreListener(this@SearchActivity)
                loadMoreModule.loadMoreView = CustomLoadMoreView()
            }
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        // 跳转详情
        adapter?.run {
            val entity = data[position] as ArticleEntity
            BrowserActivity.launch(this@SearchActivity, entity.link)
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        when (view.id) {
            R.id.ibt_star -> {
                if (mIsLogin) {
                    //收藏
                    adapter.run {
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

    override fun onLoadMore() {
        // 加载更多
        mPageNum++
        mViewModel.searchWithKeyword(mPageNum, mKeyword)
    }

    override val startObserve: SearchViewModel.() -> Unit = {
        mArticleList.observe(this@SearchActivity, Observer { articleList ->
            articleList?.let {
                swipeRefreshLayout.visibility = View.VISIBLE
                viewVisibleExt(R.id.scroll_view, false)
                if (mPageNum == 0) {
                    mListAdapter.setList(it.datas)
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
                webTaglayout.adapter.notifyDataChanged()
            }
        })
        mHotKeyData.observe(this@SearchActivity, Observer { list ->
            list?.let {
                mHotKeyList.clear()
                mHotKeyList.addAll(it)
                hotTaglayout.adapter.notifyDataChanged()
            }
        })
        mErrorMsg.observe(this@SearchActivity, Observer {
            it?.let { onApiFailure(it) }
        })
    }

    private fun setLoadStatus(hasNextPage: Boolean) {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        mListAdapter.updateLoadMoreStatus(hasNextPage)
    }

    override fun onApiFailure(msg: String) {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        super.onApiFailure(msg)
    }

    override fun onBackPressed() {
        if (swipeRefreshLayout.visibility == View.VISIBLE) {
            swipeRefreshLayout.visibility = View.GONE
            viewVisibleExt(R.id.scroll_view, true)
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
