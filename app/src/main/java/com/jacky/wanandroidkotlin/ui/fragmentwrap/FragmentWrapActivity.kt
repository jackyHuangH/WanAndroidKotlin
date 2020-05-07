package com.jacky.wanandroidkotlin.ui.fragmentwrap

import android.app.Activity
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.ui.project.ProjectFragment
import com.zenchn.support.managers.HFragmentManager
import com.zenchn.support.router.Router
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：fragment容器Activity
 * record：
 */
class FragmentWrapActivity : BaseActivity() {

    override fun getLayoutRes(): Int = R.layout.activity_fragment_wrap

    override fun initWidget() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        val isBlog = intent.getBooleanExtra(EXTRA_IS_BLOG, false)
        toolbar.title = if (isBlog) TITLE_BLOG else TITLE_PROJECT_TYPE

        val hFragmentManager = HFragmentManager(supportFragmentManager, R.id.fl_content)
        hFragmentManager.add(ProjectFragment.getInstance(isBlog))
    }

    fun enableSlideBack(enable: Boolean) {
        slidrInterface.apply {
            if (enable) unlock()
            else lock()
        }
    }

    companion object {
        private const val EXTRA_IS_BLOG = "EXTRA_IS_BLOG"
        private const val TITLE_BLOG = "公众号"
        private const val TITLE_PROJECT_TYPE = "项目分类"

        fun launch(from: Activity, isBlog: Boolean) {
            Router
                .newInstance()
                .from(from)
                .putBoolean(EXTRA_IS_BLOG, isBlog)
                .to(FragmentWrapActivity::class.java)
                .launch()
        }
    }
}