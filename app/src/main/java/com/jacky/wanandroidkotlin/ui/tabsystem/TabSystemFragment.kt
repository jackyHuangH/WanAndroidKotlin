package com.jacky.wanandroidkotlin.ui.tabsystem

import android.os.Bundle
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseFragment

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：体系Tab
 * record：
 */
class TabSystemFragment : BaseFragment() {

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

    override fun getLayoutRes(): Int= R.layout.fragment_tab_system

    override fun initWidget() {
    }
}