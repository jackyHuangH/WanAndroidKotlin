package com.jacky.wanandroidkotlin.ui.tabnavigation

import android.os.Bundle
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseFragment
import com.jacky.wanandroidkotlin.ui.tabsystem.TabSystemFragment

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：导航Tab
 * record：
 */
class TabNavigationFragment : BaseFragment() {

    companion object {
        fun getInstance(): TabSystemFragment {
            val fragment = TabSystemFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun lazyLoad() {
    }

    override fun getLayoutRes(): Int = R.layout.fragment_tab_system

    override fun initWidget() {
    }
}