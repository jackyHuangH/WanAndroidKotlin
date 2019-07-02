package com.jacky.wanandroidkotlin.wrapper.recyclerview

import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.jacky.wanandroidkotlin.R

/**
 * Created by hzj
 * on 2019/3/21 22:14
 * 自定义加载更多布局
 */
class CustomLoadMoreView : LoadMoreView() {
    override fun getLayoutId(): Int {
        return R.layout.recycler_load_more_view
    }

    override fun getLoadingViewId(): Int {
        return R.id.load_more_loading_view
    }

    override fun getLoadFailViewId(): Int {
        return R.id.load_more_load_fail_view
    }

    override fun getLoadEndViewId(): Int {
        return R.id.load_more_load_end_view
    }
}