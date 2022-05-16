package com.jacky.wanandroidkotlin.ui.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.ui.systemclassify.SystemListFragment
import com.jacky.wanandroidkotlin.ui.tabhome.TabHomeFragment
import com.jacky.wanandroidkotlin.ui.tablatestproject.TabLatestProjectFragment
import com.jacky.wanandroidkotlin.wrapper.getView

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：项目列表页面
 * record：
 */
class ProjectFragment : BaseVMFragment<ProjectViewModel>() {
    private lateinit var viewPager: ViewPager2
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
        viewPager = getView(R.id.viewPager)
        val tabLayout = getView<TabLayout>(R.id.tabLayout)
        viewPager.adapter =
            object : FragmentStateAdapter(
                parentFragmentManager,
                lifecycle
            ) {
                //                override fun getCount(): Int = mProjectTypeList.size
//
//                override fun getItem(position: Int): Fragment = chooseFragment(position)
//
//                override fun getPageTitle(position: Int) = mProjectTypeList[position].name
                override fun getItemCount(): Int = mProjectTypeList.size

                override fun createFragment(position: Int): Fragment = chooseFragment(position)
            }
//        tabLayout.setupWithViewPager(viewPager)
        TabLayoutMediator(tabLayout, viewPager) { tab, index ->
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
                viewPager.adapter?.notifyDataSetChanged()
            }
        })
    }
}