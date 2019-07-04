package com.jacky.wanandroidkotlin.ui.tablatestproject

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.ui.adapter.ProjectListAdapter
import com.jacky.wanandroidkotlin.ui.project.ProjectViewModel
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import kotlinx.android.synthetic.main.fragment_tab_home.*

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：最新项目Tab
 * record：
 */
class TabLatestProjectFragment : BaseVMFragment<ProjectViewModel>(), BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.RequestLoadMoreListener {

    private var mPageNum = 0
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

    override fun lazyLoad() {
        intiRecyclerView()
        initRefreshLayout()
    }

    override fun getLayoutRes(): Int = R.layout.fragment_tab_latest_project

    private fun intiRecyclerView() {
        rlv.layoutManager = LinearLayoutManager(activity)
        rlv.setHasFixedSize(true)
        mAdapter.onItemClickListener = this
        mAdapter.onItemChildClickListener = this
        mAdapter.setOnLoadMoreListener(this, rlv)
        mAdapter.setLoadMoreView(CustomLoadMoreView())
        rlv.adapter = mAdapter
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        //todo
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        //todo
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

    override fun initWidget() {
        onRefresh()
    }

    private fun onRefresh() {
        //下拉刷新时禁用加载更多
        mAdapter.setEnableLoadMore(false)
        swipe_refresh.isRefreshing = true
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