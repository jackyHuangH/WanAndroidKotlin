package com.jacky.wanandroidkotlin.ui.mycollect

import android.app.Activity
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.ui.adapter.HomeListAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.updateLoadMoreStatus
import com.jacky.wanandroidkotlin.wrapper.viewExt
import com.zenchn.support.router.Router
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.widget.VerticalItemDecoration

/**
 * @author:Hzj
 * @date  :2019-07-20
 * desc  ：我的收藏
 * record：
 */
class MyCollectActivity : BaseVMActivity<MyCollectViewModel>(), OnItemClickListener,
    OnLoadMoreListener {
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private var mPageNum = 0
    private val mListAdapter by lazy { HomeListAdapter(false) }

    override fun getLayoutId(): Int = R.layout.activity_my_collect

    override fun initWidget() {
        viewExt<Toolbar>(R.id.toolbar) {
            setNavigationOnClickListener { onBackPressed() }
            title = getString(R.string.my_collect_title)
        }
        swipeRefresh = getView<SwipeRefreshLayout>(R.id.swipe_refresh)
        initRecyclerView()
        initRefreshLayout()

        onRefresh()
    }

    private fun initRefreshLayout() {
        swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        swipeRefresh.setOnRefreshListener {
            //刷新数据
            onRefresh()
        }
    }

    private fun onRefresh() {
        //下拉刷新时禁用加载更多
        mListAdapter.loadMoreModule.isEnableLoadMore = false
        mPageNum = 0
        mViewModel.getMyCollectArticleList(mPageNum)
    }

    private fun initRecyclerView() {
        viewExt<RecyclerView>(R.id.rlv) {
            layoutManager = LinearLayoutManager(this@MyCollectActivity)
            setHasFixedSize(true)
            addItemDecoration(
                VerticalItemDecoration(
                    AndroidKit.Dimens.dp2px(10)
                )
            )
            mListAdapter.apply {
                setOnItemClickListener(this@MyCollectActivity)
                loadMoreModule.setOnLoadMoreListener(this@MyCollectActivity)
                loadMoreModule.loadMoreView = CustomLoadMoreView()
            }
            adapter = mListAdapter
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        // 跳转详情
        adapter?.run {
            val entity = data[position] as ArticleEntity
            BrowserActivity.launch(this@MyCollectActivity, entity.link)
        }
    }

    override fun onLoadMore() {
        // 加载更多
        mPageNum++
        mViewModel.getMyCollectArticleList(mPageNum)
    }

    override val startObserve: MyCollectViewModel.() -> Unit = {
        mArticleList.observe(this@MyCollectActivity, Observer { list ->
            list?.let {
                if (mPageNum == 0) {
                    mListAdapter.setNewInstance(it.datas)
                } else {
                    mListAdapter.addData(it.datas)
                }
                setLoadStatus(it.over.not())
            }
        })
        mErrorMsg.observe(this@MyCollectActivity, Observer { msg ->
            msg?.let { onApiFailure(it) }
        })
    }

    private fun setLoadStatus(hasNextPage: Boolean) {
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
        }
        mListAdapter.updateLoadMoreStatus(hasNextPage)
    }

    override fun onApiFailure(msg: String) {
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
        }
        super.onApiFailure(msg)
    }

    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(MyCollectActivity::class.java)
                .launch()
        }
    }
}