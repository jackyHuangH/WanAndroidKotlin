package com.jacky.wanandroidkotlin.wrapper.recyclerview

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
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
