package com.jacky.wanandroidkotlin.ui.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import com.jacky.wanandroidkotlin.R
import q.rorbin.verticaltablayout.adapter.SimpleTabAdapter
import q.rorbin.verticaltablayout.widget.ITabView

/**
 * @author:Hzj
 * @date  :2019-07-18
 * desc  ：垂直tab Adapter
 * record：
 */
class NavVerticalTabAdapter(private val mTabs: List<String>, private val mContext: Context) : SimpleTabAdapter() {

    private val mColorSelect by lazy { ContextCompat.getColor(mContext, R.color.colorAccent) }
    private val mColorNormal by lazy { ContextCompat.getColor(mContext, R.color.black_white) }

    override fun getTitle(position: Int): ITabView.TabTitle {
        return ITabView.TabTitle.Builder()
            .setContent(mTabs[position])
            .setTextColor(mColorSelect, mColorNormal)
            .build()
    }

    override fun getCount(): Int = mTabs.size
}