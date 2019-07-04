package com.jacky.wanandroidkotlin.ui.project

import android.os.Bundle
import androidx.lifecycle.Observer
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：项目列表页面
 * record：
 */
class ProjectFragment : BaseVMFragment<ProjectViewModel>() {

    override fun provideViewModelClass(): Class<ProjectViewModel>? = ProjectViewModel::class.java

    override fun getLayoutRes(): Int = R.layout.fragment_project

    companion object {
        private const val EXTRA_IS_BLOG = "EXTRA_IS_BLOG"

        fun getInstance(isBlog: Boolean): ProjectFragment {
            val fragment = ProjectFragment()
            val bundle = Bundle()
            bundle.putBoolean(EXTRA_IS_BLOG, isBlog)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun lazyLoad() {
        val isBlog = arguments?.getBoolean(EXTRA_IS_BLOG, false)
        isBlog?.let {
            if (it) mViewModel.getBlogType()
            else mViewModel.getProjectType()
        }
    }

    override fun initWidget() {
    }

    override fun startObserve() {
        mViewModel.apply {
            mTabList.observe(this@ProjectFragment, Observer {

            })
        }
    }
}