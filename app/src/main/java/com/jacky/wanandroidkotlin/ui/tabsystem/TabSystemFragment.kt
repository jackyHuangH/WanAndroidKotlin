package com.jacky.wanandroidkotlin.ui.tabsystem

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.databinding.FragmentTabSystemBinding
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.ui.adapter.SystemListAdapter
import com.jacky.wanandroidkotlin.ui.systemclassify.SystemClassifyActivity
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.widget.VerticalItemDecoration

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：体系Tab
 * record：
 */
class TabSystemFragment : BaseVMFragment<FragmentTabSystemBinding, TabSystemViewModel>(),
    OnItemClickListener {

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
        initRecyclerView()
        initRefreshLayout()
        onRefresh()
        mViewBinding.swipeRefresh.isRefreshing = true
    }

    private fun initRecyclerView() {
        mViewBinding.rlv.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            if (itemDecorationCount <= 0) {
                addItemDecoration(VerticalItemDecoration(AndroidKit.Dimens.dp2px(10)))
            }
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
        mViewBinding.swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        mViewBinding.swipeRefresh.setOnRefreshListener {
            //刷新数据
            onRefresh()
        }
    }

    private fun onRefresh() {
        mViewModel.getSystemTreeList()
    }

    override val startObserve: TabSystemViewModel.() -> Unit = {
        mTreeList.observe(this@TabSystemFragment, Observer {
            mViewBinding.swipeRefresh.isRefreshing = false
            it.run {
                mAdapter.setList(it)
                //计算rv高度
                mViewBinding.rlv.measure(0, 0)
                val measuredHeight = mViewBinding.rlv.measuredHeight
                Log.d("TabSys","rlv h:$measuredHeight")
            }
        })
        mErrorMsg.observe(this@TabSystemFragment, Observer {
            onApiFailure(it)
        })
    }

    override fun onApiFailure(msg: String) {
        if (mViewBinding.swipeRefresh.isRefreshing) {
            mViewBinding.swipeRefresh.isRefreshing = false
        }
        super.onApiFailure(msg)
    }
}