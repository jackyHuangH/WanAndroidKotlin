package com.jacky.wanandroidkotlin.ui.tabnavigation

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.ui.adapter.NavVerticalTabAdapter
import com.jacky.wanandroidkotlin.ui.adapter.NavigationAdapter
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.widget.VerticalItemDecoration
import kotlinx.android.synthetic.main.fragment_tab_navigation.*
import q.rorbin.verticaltablayout.VerticalTabLayout
import q.rorbin.verticaltablayout.widget.TabView

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：导航Tab
 * record：
 */
class TabNavigationFragment : BaseVMFragment<NavViewModel>() {
    private val mNavAdapter by lazy { NavigationAdapter() }
    private val mLayoutManager by lazy { LinearLayoutManager(activity) }

    companion object {
        fun getInstance(): TabNavigationFragment {
            val fragment = TabNavigationFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_tab_navigation

    override fun lazyLoad() {
        initRlv()
        initTab()
        mViewModel.getNavigation()
    }

    private fun initRlv() {
        rlv.apply {
            layoutManager = mLayoutManager
            addItemDecoration(
                VerticalItemDecoration(
                    AndroidKit.Dimens.dp2px(10)
                )
            )
            adapter = mNavAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    //监听列表滑动，改变tab选中状态
                    val firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition()
                    vertical_tab.setTabSelected(firstVisibleItemPosition, false)
                }
            })
        }
    }

    private fun initTab() {
        vertical_tab.addOnTabSelectedListener(object : VerticalTabLayout.OnTabSelectedListener {
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
            position <= firstPosition || position >= lastPosition -> rlv.smoothScrollToPosition(
                position
            )
            else -> rlv.run {
                smoothScrollBy(
                    0,
                    this.getChildAt(position - firstPosition).top - AndroidKit.Dimens.dp2px(10)
                )
            }
        }
    }

    override val startObserve: NavViewModel.() -> Unit = {
        mNavList.observe(this@TabNavigationFragment, Observer { list ->
            list?.let {
                val tabAdapter =
                    activity?.let { ctx -> NavVerticalTabAdapter(it.map { it.name }, ctx) }
                vertical_tab.setTabAdapter(tabAdapter)
                mNavAdapter.setNewData(list)
            }
        })
        mErrorMsg.observe(this@TabNavigationFragment, Observer { msg ->
            msg?.let { onApiFailure(it) }
        })
    }

    override fun onApiFailure(msg: String) {
        hideProgress()
        super.onApiFailure(msg)
    }
}