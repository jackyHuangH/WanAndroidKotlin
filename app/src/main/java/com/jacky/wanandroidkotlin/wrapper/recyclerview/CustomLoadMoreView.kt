package com.jacky.wanandroidkotlin.wrapper.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jacky.wanandroidkotlin.R

/**
 * Created by hzj
 * on 2019/3/21 22:14
 * 自定义加载更多布局
 */
class CustomLoadMoreView : BaseLoadMoreView() {

    override fun getLoadComplete(holder: BaseViewHolder): View =
        holder.getView(R.id.load_more_load_complete_view)

    override fun getLoadEndView(holder: BaseViewHolder): View =
        holder.getView(R.id.load_more_load_end_view)

    override fun getLoadFailView(holder: BaseViewHolder): View =
        holder.getView(R.id.load_more_load_fail_view)

    override fun getLoadingView(holder: BaseViewHolder): View =
        holder.getView(R.id.load_more_loading_view)

    override fun getRootView(parent: ViewGroup): View {
        // 整个 LoadMore 布局
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_load_more_view, parent, false);
    }
}