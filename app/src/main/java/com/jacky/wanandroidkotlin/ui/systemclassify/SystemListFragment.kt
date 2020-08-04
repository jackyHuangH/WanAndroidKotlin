package com.jacky.wanandroidkotlin.ui.systemclassify

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.ui.adapter.HomeListAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.tabsystem.TabSystemViewModel
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.widget.VerticalItemDecoration
import kotlinx.android.synthetic.main.fragment_tab_home.*

/**
 * @author:Hzj
 * @date  :2019/7/5/005
 * desc  ：体系列表 ,公众号分类页面共用
 * record：
 */
class SystemListFragment : BaseVMFragment<TabSystemViewModel>(),
    BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.RequestLoadMoreListener {

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
        initRecyclerView()
        initRefreshLayout()
        onRefresh()
        swipe_refresh.isRefreshing = true
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
        mHomeAdapter.setEnableLoadMore(false)
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
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        if (hasNextPage) {
            mHomeAdapter.loadMoreComplete()
        } else {
            mHomeAdapter.loadMoreEnd()
        }
        mHomeAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        rlv.layoutManager = LinearLayoutManager(activity)
        rlv.setHasFixedSize(true)
        rlv.addItemDecoration(
            VerticalItemDecoration(
                AndroidKit.Dimens.dp2px(10)
            )
        )
        mHomeAdapter.onItemClickListener = this
        mHomeAdapter.onItemChildClickListener = this
        mHomeAdapter.setOnLoadMoreListener(this, rlv)
        mHomeAdapter.setLoadMoreView(CustomLoadMoreView())
        rlv.adapter = mHomeAdapter
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
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        super.onApiFailure(msg)
    }
}