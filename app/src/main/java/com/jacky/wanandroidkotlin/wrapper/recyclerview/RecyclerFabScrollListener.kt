package com.jacky.wanandroidkotlin.wrapper.recyclerview

import android.util.Log
import androidx.recyclerview.widget.RecyclerView

/**
 * @author:Hzj
 * @date :2019/8/27/027
 * desc  ：FloatingActionButton随recyclerView上滑隐藏,下滑显示
 * record：dy > 0 ：手指向上滚动,列表滚动显示下面的内容,dy < 0 ：手指向下滚动,列表滚动显示上面的内容
 */
class RecyclerFabScrollListener(private val fabVisibleListener: ((Boolean) -> Unit)? = null) :
    RecyclerView.OnScrollListener() {

    //Y滑动的距离累加
    private var mDistanceY = 0

    //fab是否可见
    private var mIsFabVisible = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (mDistanceY > THRESHOLD && mIsFabVisible) {
            //隐藏fab
            mIsFabVisible = false
            fabVisibleListener?.invoke(false)
            mDistanceY = 0
        } else if (mDistanceY < -THRESHOLD * 2 && !mIsFabVisible) {
            //显示fab
            mIsFabVisible = true
            fabVisibleListener?.invoke(true)
            mDistanceY = 0
        }
        if (mIsFabVisible && dy > 0) {
            //可见并且向上滑动时
            mDistanceY += dy
        } else if (!mIsFabVisible && dy < 0) {
            //不可见并且向下滑动时
            mDistanceY += dy
        }
//        Log.d(
//            TAG,
//            "onScrolled: mDistanceY=$mDistanceY"
//        )
    }

    companion object {
        private const val TAG = "RecyclerFabScroll"

        //显示隐藏的滑动阈值
        private const val THRESHOLD = 10
    }

}