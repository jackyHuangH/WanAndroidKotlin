package com.jacky.support.managers

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle

/**
 * 管理fragment切换的manager
 */
class FragmentSwitchHelper(
    // 管理类FragmentManager
    private val mFragmentManager: FragmentManager?,
    // 容器布局id containerViewId
    @param:IdRes private val mContainerViewId: Int
) {

    /**
     * 添加Fragment
     * 此方法必须先调用
     */
    fun add(fragment: Fragment?) {
        checkNotNull(fragment) { "you must call add(Fragment fragment) first!" }

        // 开启事物
        mFragmentManager?.beginTransaction()?.apply {
            if (fragment.isAdded) {
                show(fragment)
            } else {
                //已添加，需要先移除
                remove(fragment)
                // 第一个参数是Fragment的容器id，需要添加的Fragment
                add(mContainerViewId, fragment)
            }
            // 一定要commit
            commit()
            setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
        }
    }

    /**
     * 切换显示Fragment
     */
    fun switchFragment(fragment: Fragment) {
        // 开启事物
        mFragmentManager?.beginTransaction()?.apply {
            // 1.先隐藏当前所有的Fragment
            val childFragments = mFragmentManager.fragments
            childFragments.filter { it.isAdded }.forEach { childFragment ->
                hide(childFragment)
            }

            // 2.如果容器里面没有我们就添加，否则显示
            if (fragment.isAdded || childFragments.contains(fragment)) {
                show(fragment)
            } else {
                add(mContainerViewId, fragment)
            }
            // 替换Fragment
            // fragmentTransaction.replace(R.id.main_tab_fl,mHomeFragment);
            // 一定要commit
            commit()
            setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
        }
    }

    /**
     * 清空之前的fragments
     */
    fun clearPreviousFragments() {
        val fragmentTransaction = mFragmentManager?.beginTransaction()
        val fragments = mFragmentManager?.fragments?.forEach { frag ->
            fragmentTransaction?.apply { remove(frag) }
        }
        fragmentTransaction?.commitNowAllowingStateLoss()
    }
}