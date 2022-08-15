package com.jacky.wanandroidkotlin.ui.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.databinding.FragmentProjectBinding
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.ui.systemclassify.SystemListFragment
import com.jacky.wanandroidkotlin.ui.tabhome.TabHomeFragment
import com.jacky.wanandroidkotlin.ui.tablatestproject.TabLatestProjectFragment

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：项目列表页面
 * record：
 */
class ProjectFragment : BaseVMFragment<FragmentProjectBinding, ProjectViewModel>() {
    private val mProjectTypeList = mutableListOf<TreeParentEntity>()
    private val mIsBlog by lazy { arguments?.getBoolean(EXTRA_IS_BLOG, false) }

    override fun getLayoutId(): Int = R.layout.fragment_project

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
        mIsBlog?.let {
            if (it) mViewModel.getBlogType()
            else mViewModel.getProjectType()
        }
    }

    override fun initWidget() {
        mViewBinding.viewPager2.adapter =
            object : FragmentStateAdapter(
                parentFragmentManager,
                lifecycle
            ) {
                override fun getItemCount(): Int = mProjectTypeList.size

                override fun createFragment(position: Int): Fragment = chooseFragment(position)
            }
        TabLayoutMediator(mViewBinding.tabLayout, mViewBinding.viewPager2) { tab, index ->
            tab.text = mProjectTypeList[index].name
        }.attach()
    }

    private fun chooseFragment(position: Int): Fragment {
        mIsBlog?.apply {
            return if (this) SystemListFragment.getInstance(mProjectTypeList[position].id, true)
            else TabLatestProjectFragment.getInstance(mProjectTypeList[position].id, false)
        }
        return TabHomeFragment()
    }

    override val startObserve: ProjectViewModel.() -> Unit = {
        mTabList.observe(this@ProjectFragment, Observer { list ->
            list?.let {
                mProjectTypeList.clear()
                mProjectTypeList.addAll(it)
                mViewBinding.viewPager2.adapter?.notifyDataSetChanged()
            }
        })
    }
}