package com.jacky.wanandroidkotlin.ui.search

import android.app.Activity
import com.gyf.immersionbar.ImmersionBar
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.zenchn.support.router.Router

/**
 * 搜索
 */
class SearchActivity : BaseActivity() {
    override fun getLayoutRes(): Int = R.layout.activity_search

    override fun initStatusBar() {
        //不适配状态栏
        ImmersionBar.with(this)
            .fullScreen(true)
            .init()
    }

    override fun initWidget() {
    }

    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(SearchActivity::class.java)
                .launch()
        }
    }
}
