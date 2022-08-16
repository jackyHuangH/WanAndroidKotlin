package com.jacky.wanandroidkotlin.ui.systemclassify

import android.app.Activity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.databinding.ActivitySystemClassifyBinding
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity
import com.jacky.wanandroidkotlin.ui.adapter.BaseViewPager2Adapter
import com.zenchn.support.router.Router

/**
 * @author:Hzj
 * @date  :2019/7/5/005
 * desc  ：体系分类页面
 * record：
 */
class SystemClassifyActivity : BaseActivity<ActivitySystemClassifyBinding>() {
    private lateinit var mTreeParent: TreeParentEntity

    override fun getLayoutId(): Int = R.layout.activity_system_classify

    override fun initWidget() {
        mTreeParent = intent.getSerializableExtra(EXTRA_TREE_PARENT) as TreeParentEntity
        mViewBinding.toolbar.apply {
            title = mTreeParent.name
            setNavigationOnClickListener { onBackPressed() }
        }
        initViewPager()
    }

    private fun initViewPager() {
        //这里viewpager2+fragment
        if (mTreeParent.children.isEmpty()) {
            showMessage("暂无数据")
            return
        }
        val fragmentList = mTreeParent.children.map { item ->
            SystemListFragment.getInstance(item.id, false)
        }
        mViewBinding.viewPager2.adapter =
            BaseViewPager2Adapter(supportFragmentManager, lifecycle, fragmentList)
        //TabLayout绑定viewpager2
        TabLayoutMediator(mViewBinding.tabLayout, mViewBinding.viewPager2) { tab, index ->
            //绑定标题
            tab.text = mTreeParent.children[index].name
        }.attach()
        //禁用viewpager2滑动
//        mViewBinding.viewPager2.isUserInputEnabled=false
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