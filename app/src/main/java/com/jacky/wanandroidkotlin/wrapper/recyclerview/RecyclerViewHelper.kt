package com.jacky.wanandroidkotlin.wrapper.recyclerview

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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
}

/**
 * 瀑布流item间隔
 */
class StaggeredDividerItemDecoration(val context: Context,val spaceDp: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
//        int position = parent.getChildAdapterPosition(view);
        val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        // 获取item在span中的下标
        val spanIndex = params.spanIndex
        val intervalPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spaceDp.toFloat(), context.getResources().getDisplayMetrics()).toInt()

        // 中间间隔
        if (spanIndex % 2 == 0) {
            outRect.left = 0
            outRect.right = intervalPx / 2
        } else {
        // item为奇数位，设置其左间隔为5dp
            outRect.left = intervalPx / 2
            outRect.right = 0
        }
        // 下方间隔
        outRect.bottom = intervalPx
    }
}