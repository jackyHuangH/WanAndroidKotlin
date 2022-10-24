package com.jacky.wanandroidkotlin.ui.tabnavigation

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jacky.support.utils.AndroidKit
import com.jacky.support.widget.VerticalItemDecoration
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.databinding.FragmentTabNavigationBinding
import com.jacky.wanandroidkotlin.ui.adapter.NavVerticalTabAdapter
import com.jacky.wanandroidkotlin.ui.adapter.NavigationAdapter
import q.rorbin.verticaltablayout.VerticalTabLayout
import q.rorbin.verticaltablayout.widget.TabView

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：导航Tab
 * record：
 */
class TabNavigationFragment : BaseVMFragment<FragmentTabNavigationBinding, NavViewModel>() {

    private val mNavAdapter by lazy { NavigationAdapter() }
    private val mLayoutManager by lazy { LinearLayoutManager(requireContext()) }

    companion object {
        fun getInstance(): TabNavigationFragment {
            val fragment = TabNavigationFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_tab_navigation

    override fun lazyLoad() {
        mViewBinding.swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        mViewBinding.swipeRefresh.setOnRefreshListener {
            //刷新数据
            mViewModel.getNavigation()
        }
        initRlv()
        initTab()
        mViewModel.getNavigation()
        mViewBinding.swipeRefresh.isRefreshing = true
    }

    private fun initRlv() {
        mViewBinding.rlv.apply {
            layoutManager = mLayoutManager
            if (itemDecorationCount <= 0) {
                addItemDecoration(
                    VerticalItemDecoration(
                        AndroidKit.Dimens.dp2px(10)
                    )
                )
            }
            adapter = mNavAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    //监听列表滑动，改变tab选中状态
                    val firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition()
                    mViewBinding.verticalTab.setTabSelected(firstVisibleItemPosition, false)
                }
            })
        }
    }

    private fun initTab() {
        mViewBinding.verticalTab.addOnTabSelectedListener(object :
            VerticalTabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabView?, position: Int) {
            }

            override fun onTabSelected(tab: TabView?, position: Int) {
                scrollTo(position)
            }
        })
    }

    private fun scrollTo(position: Int) {
        val firstPosition = mLayoutManager.findFirstVisibleItemPosition()
        val lastPosition = mLayoutManager.findLastVisibleItemPosition()
        when {
            position <= firstPosition || position >= lastPosition -> mViewBinding.rlv.smoothScrollToPosition(
                position
            )
            else -> mViewBinding.rlv.run {
                smoothScrollBy(
                    0,
                    this.getChildAt(position - firstPosition).top - AndroidKit.Dimens.dp2px(10)
                )
            }
        }
    }

    override val startObserve: NavViewModel.() -> Unit = {
        mNavList.observe(this@TabNavigationFragment, Observer { list ->
            mViewBinding.swipeRefresh.isRefreshing = false
            list?.let {
                val tabAdapter =
                    activity?.let { ctx -> NavVerticalTabAdapter(it.map { it.name }, ctx) }
                mViewBinding.verticalTab.setTabAdapter(tabAdapter)
                mNavAdapter.setList(list)
            }
        })
        mErrorMsg.observe(this@TabNavigationFragment, Observer { msg ->
            msg?.let { onApiFailure(it) }
        })
    }

    override fun onApiFailure(msg: String) {
        if (mViewBinding.swipeRefresh.isRefreshing) {
            mViewBinding.swipeRefresh.isRefreshing = false
        }
        hideProgress()
        super.onApiFailure(msg)
    }
}