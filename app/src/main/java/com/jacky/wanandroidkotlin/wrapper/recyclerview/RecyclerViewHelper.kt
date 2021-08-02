package com.jacky.wanandroidkotlin.wrapper.recyclerview

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jacky.wanandroidkotlin.R


/**
 * @author:Hzj
 * @date :2019/04/12 10:11
 * desc  ：
 * record：
 */
object RecyclerViewHelper {

    /**
     * 提供默认的 emptyView
     * @param context
     * @param rlv
     * @return
     */
    fun getCommonEmptyView(rlv: RecyclerView): View =
        LayoutInflater.from(rlv.context).inflate(R.layout.recy_empty_view, rlv, false)

}


/**
 * 更新加载更多状态
 */
fun BaseQuickAdapter<*, *>.updateLoadMoreStatus(hasNextPage: Boolean) {
    if (hasNextPage) {
        loadMoreModule.loadMoreComplete()
    } else {
        loadMoreModule.loadMoreEnd()
    }
    notifyDataSetChanged()
}