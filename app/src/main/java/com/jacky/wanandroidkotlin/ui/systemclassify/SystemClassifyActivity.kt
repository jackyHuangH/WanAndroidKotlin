package com.jacky.wanandroidkotlin.ui.systemclassify

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.viewExt
import com.zenchn.support.router.Router

/**
 * @author:Hzj
 * @date  :2019/7/5/005
 * desc  ：体系分类页面
 * record：
 */
class SystemClassifyActivity : BaseActivity() {
    private lateinit var mTreeParent: TreeParentEntity
    override fun getLayoutId(): Int = R.layout.activity_system_classify

    override fun initWidget() {
        mTreeParent = intent.getSerializableExtra(EXTRA_TREE_PARENT) as TreeParentEntity
        viewExt<Toolbar>(R.id.toolbar) {
            title = mTreeParent.name
            setNavigationOnClickListener { onBackPressed() }
        }
        initViewPager()
    }

    private fun initViewPager() {
        val viewPager = getView<ViewPager>(R.id.viewPager)
        val tabLayout = getView<TabLayout>(R.id.tab_layout)
        //这里fragment数量较多，使用FragmentStatePagerAdapter
        viewPager.adapter = object : FragmentStatePagerAdapter(
            supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getItem(position: Int): Fragment {
                return SystemListFragment.getInstance(mTreeParent.children[position].id, false)
            }

            override fun getCount(): Int = mTreeParent.children.size

            override fun getPageTitle(position: Int): CharSequence? =
                mTreeParent.children[position].name
        }
        tabLayout.setupWithViewPager(viewPager)
    }

    companion object {
        private const val EXTRA_TREE_PARENT = "EXTRA_TREE_PARENT"

        fun launch(from: Activity, treeParentEntity: TreeParentEntity) {
            Router
                .newInstance()
                .from(from)
                .putSerializable(EXTRA_TREE_PARENT, treeParentEntity)
                .to(SystemClassifyActivity::class.java)
                .launch()
        }
    }
}