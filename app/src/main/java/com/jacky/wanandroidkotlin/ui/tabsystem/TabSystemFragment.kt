package com.jacky.wanandroidkotlin.ui.tabsystem

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.ui.adapter.SystemListAdapter
import com.jacky.wanandroidkotlin.ui.systemclassify.SystemClassifyActivity
import com.jacky.wanandroidkotlin.wrapper.getView
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.widget.VerticalItemDecoration

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：体系Tab
 * record：
 */
class TabSystemFragment : BaseVMFragment<TabSystemViewModel>(), OnItemClickListener {
    private lateinit var rlv: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val mAdapter by lazy { SystemListAdapter() }

    companion object {
        fun getInstance(): TabSystemFragment {
            val fragment = TabSystemFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_tab_system

    override fun lazyLoad() {
        rlv = getView<RecyclerView>(R.id.rlv)
        swipeRefreshLayout = getView<SwipeRefreshLayout>(R.id.swipe_refresh)
        initRecyclerView()
        initRefreshLayout()
        onRefresh()
        swipeRefreshLayout.isRefreshing = true
    }

    private fun initRecyclerView() {
        rlv.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            addItemDecoration(VerticalItemDecoration(AndroidKit.Dimens.dp2px(10)))
            adapter = mAdapter.apply {
                setOnItemClickListener(this@TabSystemFragment)
            }
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        // 跳转详情
        adapter.run {
            val entity = data[position] as TreeParentEntity
            activity?.let { SystemClassifyActivity.launch(it, entity) }
        }
    }

    private fun initRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            //刷新数据
            onRefresh()
        }
    }

    private fun onRefresh() {
        mViewModel.getSystemTreeList()
    }

    override val startObserve: TabSystemViewModel.() -> Unit = {
        mTreeList.observe(this@TabSystemFragment, Observer {
            swipeRefreshLayout.isRefreshing = false
            it.run { mAdapter.setNewInstance(it) }
        })
        mErrorMsg.observe(this@TabSystemFragment, Observer {
            onApiFailure(it)
        })
    }

    override fun onApiFailure(msg: String) {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        super.onApiFailure(msg)
    }
}