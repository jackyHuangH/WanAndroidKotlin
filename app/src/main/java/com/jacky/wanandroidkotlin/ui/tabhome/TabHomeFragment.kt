package com.jacky.wanandroidkotlin.ui.tabhome

import android.os.Bundle
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseFragment
import com.jacky.wanandroidkotlin.ui.tablatestproject.TabLatestProjectFragment

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：首页Tab
 * record：
 */
class TabHomeFragment : BaseFragment() {

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

    override fun getLayoutRes(): Int= R.layout.fragment_tab_home

    override fun initWidget() {
        initBanner()

    }

    private fun initBanner() {

    }
}