package com.jacky.wanandroidkotlin.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：fragment+viewpager Adapter
 * desc  ：viewpager+fragment切换adapter
 * record：该类内的每一个生成的 Fragment 都将保存在内存之中，
 * 因此适用于那些相对静态的页，数量也比较少的那种；
 * 如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，
 * 应该继承FragmentStatePagerAdapter。
 */
class BaseFragmentPagerAdapter(
    fm: FragmentManager,
    fragments: List<Fragment>, titles: List<String>
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var mFragments: List<Fragment>? = fragments
    private var mTitles: List<String>? = titles

    override fun getPageTitle(position: Int): CharSequence? {
        return if (mTitles != null) {
            mTitles!![position]
        } else {
            ""
        }
    }

    override fun getItem(position: Int): Fragment {
        return mFragments!![position]
    }

    override fun getCount(): Int {
        return mFragments?.size ?: 0
    }
}

/**
 * viewpager2 pagerAdapter
 */
class BaseFragmentPager2Adapter(
    fm: FragmentManager, lifecycle: Lifecycle,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}