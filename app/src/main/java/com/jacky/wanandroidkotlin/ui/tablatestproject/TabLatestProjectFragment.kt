package com.jacky.wanandroidkotlin.ui.tablatestproject

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.databinding.FragmentTabLatestProjectBinding
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.ui.adapter.ProjectListAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.project.ProjectViewModel
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.RecyclerViewHelper
import com.jacky.wanandroidkotlin.wrapper.recyclerview.updateLoadMoreStatus
import com.jacky.wanandroidkotlin.wrapper.viewExt
import com.jacky.support.utils.AndroidKit
import com.jacky.support.widget.VerticalItemDecoration

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：最新项目Tab,项目分类公用页面
 * record：
 */
class TabLatestProjectFragment :
    BaseVMFragment<FragmentTabLatestProjectBinding, ProjectViewModel>(),
    OnItemClickListener, OnItemChildClickListener, OnLoadMoreListener {
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private var mPageNum = 0
    private var mIsDownRefresh = true//是否是下拉刷新数据
    private val mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)
    private val mAdapter: ProjectListAdapter by lazy { ProjectListAdapter() }
    private val mCId by lazy { arguments?.getInt(EXTRA_CID) }
    private val mIsLatest by lazy { arguments?.getBoolean(EXTRA_IS_LASTED) } // 区分是最新项目 还是项目分类

    companion object {
        private const val EXTRA_CID = "EXTRA_CID"
        private const val EXTRA_IS_LASTED = "EXTRA_IS_LASTED"//是否是最新项目页面，默认true

        fun getInstance(cid: Int, isLatest: Boolean): TabLatestProjectFragment {
            val fragment = TabLatestProjectFragment()
            val bundle = Bundle()
            bundle.putInt(EXTRA_CID, cid)
            bundle.putBoolean(EXTRA_IS_LASTED, isLatest)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun lazyLoad() {
        swipeRefreshLayout = getView<SwipeRefreshLayout>(R.id.swipe_refresh)
        intiRecyclerView()
        initRefreshLayout()
        onRefresh()
        swipeRefreshLayout?.isRefreshing = true
    }

    override fun getLayoutId(): Int = R.layout.fragment_tab_latest_project

    private fun intiRecyclerView() {
        viewExt<RecyclerView>(R.id.rlv) {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            if (itemDecorationCount <= 0) {
                addItemDecoration(
                    VerticalItemDecoration(
                        AndroidKit.Dimens.dp2px(10)
                    )
                )
            }
            mAdapter.apply {
                setOnItemClickListener(this@TabLatestProjectFragment)
                addChildClickViewIds(R.id.ibt_star)
                setOnItemChildClickListener(this@TabLatestProjectFragment)
                loadMoreModule.setOnLoadMoreListener(this@TabLatestProjectFragment)
                loadMoreModule.loadMoreView = CustomLoadMoreView()
                setEmptyView(RecyclerViewHelper.getCommonEmptyView(this@viewExt))
            }
            adapter = mAdapter
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        // 跳转详情
        adapter.run {
            val entity = data[position] as ArticleEntity
            activity?.let { BrowserActivity.launch(it, entity.link) }
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        when (view?.id) {
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
                    activity?.let { LoginActivity.launch(it) }
                }
            }
        }
    }

    override fun onLoadMore() {
        mIsDownRefresh = false
        mIsLatest?.run {
            if (this) {
                mViewModel.getLastedProjectList(++mPageNum)
            } else {
                // 项目分类更多
                mCId?.let { mViewModel.getProjectList(++mPageNum, it) }
            }
        }
    }

    private fun initRefreshLayout() {
        swipeRefreshLayout?.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout?.setOnRefreshListener {
            //刷新数据
            onRefresh()
        }
    }

    private fun onRefresh() {
        //下拉刷新时禁用加载更多
        mIsDownRefresh = true
        mAdapter.loadMoreModule.isEnableLoadMore = false
        mIsLatest?.run {
            if (this) {
                //最新项目
                mPageNum = 0
                mViewModel.getLastedProjectList(mPageNum)
            } else {
                // 此处有坑！！！项目分类，分页从1开始
                mPageNum = 1
                mCId?.let { mViewModel.getProjectList(mPageNum, it) }
            }
        }
    }

    override val startObserve: ProjectViewModel.() -> Unit = {
        mArticleList.observe(this@TabLatestProjectFragment, Observer { list ->
            list?.let {
                if (mIsDownRefresh) {
                    mAdapter.setList(it.datas)
                } else {
                    mAdapter.addData(it.datas)
                }
                setLoadStatus(!it.over)
            }
        })
        mErrorMsg.observe(this@TabLatestProjectFragment, Observer { msg ->
            msg?.let { onApiFailure(it) }
        })
    }

    private fun setLoadStatus(hasNextPage: Boolean) {
        if (swipeRefreshLayout?.isRefreshing == true) {
            swipeRefreshLayout?.isRefreshing = false
        }
        mAdapter.updateLoadMoreStatus(hasNextPage)
    }

    override fun onApiFailure(msg: String) {
        if (swipeRefreshLayout?.isRefreshing == true) {
            swipeRefreshLayout?.isRefreshing = false
        }
        super.onApiFailure(msg)
    }
}