package com.jacky.wanandroidkotlin.ui.tablatestproject

import android.os.Bundle
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseFragment

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：最新项目Tab
 * record：
 */
class TabLatestProjectFragment : BaseFragment() {

    companion object {
        fun getInstance(): TabLatestProjectFragment {
            val fragment = TabLatestProjectFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun lazyLoad() {
    }

    override fun getLayoutRes(): Int= R.layout.fragment_tab_latest_project

    override fun initWidget() {
    }
}