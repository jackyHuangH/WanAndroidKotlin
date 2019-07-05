package com.jacky.wanandroidkotlin.ui.tabsystem

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.ui.adapter.SystemListAdapter
import com.jacky.wanandroidkotlin.ui.systemclassify.SystemClassifyActivity
import com.jacky.wanandroidkotlin.wrapper.recyclerview.SpaceItemDecoration
import com.zenchn.support.kit.AndroidKit
import kotlinx.android.synthetic.main.fragment_tab_system.*

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：体系Tab
 * record：
 */
class TabSystemFragment : BaseVMFragment<TabSystemViewModel>(), BaseQuickAdapter.OnItemClickListener {

    private val mAdapter by lazy { SystemListAdapter() }

    override fun provideViewModelClass(): Class<TabSystemViewModel>? = TabSystemViewModel::class.java

    companion object {
        fun getInstance(): TabSystemFragment {
            val fragment = TabSystemFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_tab_system

    override fun initWidget() {
    }

    override fun lazyLoad() {
        initRecyclerView()
        initRefreshLayout()
        onRefresh()
        swipe_refresh.isRefreshing = true
    }

    private fun initRecyclerView() {
        rlv.layoutManager = LinearLayoutManager(activity)
        rlv.setHasFixedSize(true)
        rlv.addItemDecoration(SpaceItemDecoration(AndroidKit.Dimens.dp2px(10)))
        mAdapter.onItemClickListener = this
        rlv.adapter = mAdapter
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        // 跳转详情
        adapter?.run {
            val entity = data[position] as TreeParentEntity
            activity?.let { SystemClassifyActivity.launch(it, entity) }
        }
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
        mViewModel.getSystemTreeList()
    }

    override fun startObserve() {
        mViewModel.apply {
            mTreeList.observe(this@TabSystemFragment, Observer {
                swipe_refresh.isRefreshing = false
                it.run { mAdapter.setNewData(it) }
            })
            mErrorMsg.observe(this@TabSystemFragment, Observer {
                onApiFailure(it)
            })
        }
    }

    override fun onApiFailure(msg: String) {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        super.onApiFailure(msg)
    }
}