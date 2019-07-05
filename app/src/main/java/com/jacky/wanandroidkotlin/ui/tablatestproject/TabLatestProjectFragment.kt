package com.jacky.wanandroidkotlin.ui.tablatestproject

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.ui.adapter.ProjectListAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.project.ProjectViewModel
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.SpaceItemDecoration
import com.zenchn.support.kit.AndroidKit
import kotlinx.android.synthetic.main.fragment_tab_latest_project.*

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：最新项目Tab
 * record：
 */
class TabLatestProjectFragment : BaseVMFragment<ProjectViewModel>(), BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.RequestLoadMoreListener {

    private var mPageNum = 0
    private val mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)
    private val mAdapter: ProjectListAdapter by lazy { ProjectListAdapter() }
    override fun provideViewModelClass(): Class<ProjectViewModel>? = ProjectViewModel::class.java

    companion object {
        fun getInstance(): TabLatestProjectFragment {
            val fragment = TabLatestProjectFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initWidget() {
    }

    override fun lazyLoad() {
        intiRecyclerView()
        initRefreshLayout()
        onRefresh()
        swipe_refresh.isRefreshing = true
    }

    override fun getLayoutRes(): Int = R.layout.fragment_tab_latest_project

    private fun intiRecyclerView() {
        rlv.layoutManager = LinearLayoutManager(activity)
        rlv.setHasFixedSize(true)
        rlv.addItemDecoration(SpaceItemDecoration(AndroidKit.Dimens.dp2px(10)))
        mAdapter.onItemClickListener = this
        mAdapter.onItemChildClickListener = this
        mAdapter.setOnLoadMoreListener(this, rlv)
        mAdapter.setLoadMoreView(CustomLoadMoreView())
        rlv.adapter = mAdapter
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        // 跳转详情
        adapter?.run {
            val entity = data[position] as ArticleEntity
            activity?.let { BrowserActivity.launch(it, entity.link) }
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
                    activity?.let { LoginActivity.launch(it) }
                }
            }
        }
    }

    override fun onLoadMoreRequested() {
        mViewModel.getLastedProjectList(++mPageNum)
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
        mAdapter.setEnableLoadMore(false)
        mPageNum = 0
        mViewModel.getLastedProjectList(mPageNum)
    }

    override fun startObserve() {
        mViewModel.apply {
            mArticleList.observe(this@TabLatestProjectFragment, Observer {
                it?.let {
                    if (mPageNum == 0) {
                        mAdapter.setNewData(it.datas)
                    } else {
                        mAdapter.addData(it.datas)
                    }
                    setLoadStatus(!it.over)
                }
            })
            mErrorMsg.observe(this@TabLatestProjectFragment, Observer {
                it?.let { onApiFailure(it) }
            })
        }
    }

    private fun setLoadStatus(hasNextPage: Boolean) {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        if (hasNextPage) {
            mAdapter.loadMoreComplete()
        } else {
            mAdapter.loadMoreEnd()
        }
        mAdapter.notifyDataSetChanged()
    }

    override fun onApiFailure(msg: String) {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        super.onApiFailure(msg)
    }
}