package com.jacky.wanandroidkotlin.ui.mycollect

import android.app.Activity
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.ui.adapter.HomeListAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.SpaceItemDecoration
import com.zenchn.support.kit.AndroidKit
import com.zenchn.support.router.Router
import kotlinx.android.synthetic.main.activity_my_collect.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * @author:Hzj
 * @date  :2019-07-20
 * desc  ：我的收藏
 * record：
 */
class MyCollectActivity : BaseVMActivity<MyCollectViewModel>(), BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.RequestLoadMoreListener {

    private var mPageNum = 0
    private val mListAdapter by lazy { HomeListAdapter() }

    override fun provideViewModelClass(): Class<MyCollectViewModel>? = MyCollectViewModel::class.java

    override fun getLayoutRes(): Int = R.layout.activity_my_collect

    override fun initWidget() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.title = getString(R.string.my_collect_title)

        initRecyclerView()
        initRefreshLayout()

        onRefresh()
    }

    private fun initRefreshLayout() {
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh.setOnRefreshListener {
            //刷新数据
            onRefresh()
        }
    }

    private fun onRefresh() {
        //下拉刷新时禁用加载更多
        mListAdapter.setEnableLoadMore(false)
        mPageNum = 0
        mViewModel.getMyCollectArticleList(mPageNum)
    }

    private fun initRecyclerView() {
        rlv.layoutManager = LinearLayoutManager(this)
        rlv.setHasFixedSize(true)
        rlv.addItemDecoration(SpaceItemDecoration(AndroidKit.Dimens.dp2px(10)))
        mListAdapter.onItemClickListener = this
        mListAdapter.setOnLoadMoreListener(this, rlv)
        mListAdapter.setLoadMoreView(CustomLoadMoreView())
        mListAdapter.showStar(false)
        rlv.adapter = mListAdapter
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        // 跳转详情
        adapter?.run {
            val entity = data[position] as ArticleEntity
            BrowserActivity.launch(this@MyCollectActivity, entity.link)
        }
    }

    override fun onLoadMoreRequested() {
        // 加载更多
        mPageNum++
        mViewModel.getMyCollectArticleList(mPageNum)
    }

    override fun startObserve() {
        mViewModel.apply {
            mArticleList.observe(this@MyCollectActivity, Observer { list ->
                list?.let {
                    if (mPageNum == 0) {
                        mListAdapter.setNewData(it.datas)
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

    }

    private fun setLoadStatus(hasNextPage: Boolean) {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        if (hasNextPage) {
            mListAdapter.loadMoreComplete()
        } else {
            mListAdapter.loadMoreEnd()
        }
        mListAdapter.notifyDataSetChanged()
    }

    override fun onApiFailure(msg: String) {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
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