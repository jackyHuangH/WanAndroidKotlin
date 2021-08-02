package com.jacky.wanandroidkotlin.ui.systemclassify

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
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.ui.adapter.HomeListAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.tabsystem.TabSystemViewModel
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.updateLoadMoreStatus
import com.jacky.wanandroidkotlin.wrapper.viewExt
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.widget.VerticalItemDecoration

/**
 * @author:Hzj
 * @date  :2019/7/5/005
 * desc  ：体系列表 ,公众号分类页面共用
 * record：
 */
class SystemListFragment : BaseVMFragment<TabSystemViewModel>(),
    OnItemClickListener, OnItemChildClickListener, OnLoadMoreListener {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val mCid by lazy { arguments?.getInt(EXTRA_CID) }
    private val mIsBlog by lazy { arguments?.getBoolean(EXTRA_IS_BLOG) }
    private val mHomeAdapter: HomeListAdapter by lazy { HomeListAdapter() }
    private var mPageNum = 0
    private val mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)

    override fun getLayoutId(): Int = R.layout.fragment_system_list

    companion object {
        private const val EXTRA_CID = "EXTRA_CID"
        private const val EXTRA_IS_BLOG = "EXTRA_IS_BLOG"
        fun getInstance(cid: Int, isBlog: Boolean): SystemListFragment {
            val fragment = SystemListFragment()
            val bundle = Bundle()
            bundle.putInt(EXTRA_CID, cid)
            bundle.putBoolean(EXTRA_IS_BLOG, isBlog)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initWidget() {
        swipeRefreshLayout = getView<SwipeRefreshLayout>(R.id.swipe_refresh)
        initRecyclerView()
        initRefreshLayout()
        onRefresh()
        swipeRefreshLayout.isRefreshing = true
    }

    private fun initRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            //刷新数据
            onRefresh()
        }
    }

    private fun onRefresh() {
        //下拉刷新时禁用加载更多
        mHomeAdapter.loadMoreModule.isEnableLoadMore = false
        mPageNum = 0
        mCid?.let {
            mIsBlog?.run {
                if (this) {
                    // 获取公众号文章
                    mViewModel.getBlogList(mPageNum, it)
                } else {
                    mViewModel.getSystemArticleListByCid(mPageNum, it)
                }
            }
        }
    }

    override val startObserve: TabSystemViewModel.() -> Unit = {
        //LiveData 刷新数据到页面
        mArticleList.observe(this@SystemListFragment, Observer {
            it?.let {
                if (mPageNum == 0) {
                    mHomeAdapter.setNewData(it.datas)
                } else {
                    mHomeAdapter.addData(it.datas)
                }
                setLoadStatus(!it.over)
            }
        })
        mErrorMsg.observe(this@SystemListFragment, Observer {
            it?.let { onApiFailure(it) }
        })
    }

    private fun setLoadStatus(hasNextPage: Boolean) {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        mHomeAdapter.updateLoadMoreStatus(hasNextPage)
    }

    private fun initRecyclerView() {
        viewExt<RecyclerView>(R.id.rlv) {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            addItemDecoration(
                VerticalItemDecoration(
                    AndroidKit.Dimens.dp2px(10)
                )
            )
            mHomeAdapter.apply {
                setOnItemClickListener(this@SystemListFragment)
                addChildClickViewIds(R.id.ibt_star)
                setOnItemChildClickListener(this@SystemListFragment)
                loadMoreModule.setOnLoadMoreListener(this@SystemListFragment)
                loadMoreModule.loadMoreView = CustomLoadMoreView()
            }
            adapter = mHomeAdapter
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
        // 加载更多
        mPageNum++
        mCid?.let {
            mIsBlog?.run {
                if (this) {
                    //获取公众号文章
                    mViewModel.getBlogList(mPageNum, it)
                } else {
                    mViewModel.getSystemArticleListByCid(mPageNum, it)
                }
            }
        }
    }

    override fun onApiFailure(msg: String) {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        super.onApiFailure(msg)
    }
}