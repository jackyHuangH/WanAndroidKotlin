package com.jacky.wanandroidkotlin.ui.fragmentwrap

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.databinding.ActivityFragmentWrapBinding
import com.jacky.wanandroidkotlin.ui.project.ProjectFragment
import com.jacky.wanandroidkotlin.wrapper.getView
import com.zenchn.support.managers.FragmentSwitchHelper
import com.zenchn.support.router.Router

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：fragment容器Activity
 * record：
 */
class FragmentWrapActivity : BaseActivity<ActivityFragmentWrapBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_fragment_wrap

    override fun initWidget() {
        val toolbar = getView<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        val isBlog = intent.getBooleanExtra(EXTRA_IS_BLOG, false)
        toolbar.title = getString(if (isBlog) R.string.nv_item_blog else R.string.nv_item_type)

        val hFragmentManager = FragmentSwitchHelper(supportFragmentManager, R.id.fl_content)
        hFragmentManager.add(ProjectFragment.getInstance(isBlog))
    }

    companion object {
        private const val EXTRA_IS_BLOG = "EXTRA_IS_BLOG"

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