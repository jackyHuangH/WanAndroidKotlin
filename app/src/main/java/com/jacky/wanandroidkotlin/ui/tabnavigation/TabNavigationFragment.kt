package com.jacky.wanandroidkotlin.ui.tabnavigation

import android.os.Bundle
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseFragment

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：导航Tab
 * record：
 */
class TabNavigationFragment : BaseFragment() {

    companion object {
        fun getInstance(): TabNavigationFragment {
            val fragment = TabNavigationFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun lazyLoad() {
    }

    override fun getLayoutRes(): Int = R.layout.fragment_tab_navigation

    override fun initWidget() {
    }
}