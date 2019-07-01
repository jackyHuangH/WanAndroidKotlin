package com.jacky.wanandroidkotlin.ui.main

import android.app.Activity
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.adapter.BaseFragmentPagerAdapter
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.common.Constant
import com.jacky.wanandroidkotlin.ui.search.SearchActivity
import com.jacky.wanandroidkotlin.ui.tabhome.TabHomeFragment
import com.jacky.wanandroidkotlin.ui.tablatestproject.TabLatestProjectFragment
import com.jacky.wanandroidkotlin.ui.tabnavigation.TabNavigationFragment
import com.jacky.wanandroidkotlin.ui.tabsystem.TabSystemFragment
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.util.StatusBarUtil
import com.jacky.wanandroidkotlin.wrapper.glide.GlideApp
import com.zenchn.support.router.Router
import com.zenchn.support.widget.CircleTextImageView
import kotlinx.android.synthetic.main.activity_main.*


/**
 * 首页
 */
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val mTitles = arrayListOf("首页", "最新项目", "体系", "导航")
    private var mFragments: ArrayList<Fragment> = ArrayList()

    private var mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)
    private val mUserInfo by PreferenceUtil(PreferenceUtil.KEY_USER_INFO, "")

    override fun getLayoutRes() = R.layout.activity_main

    init {
        mFragments.add(TabHomeFragment.getInstance())
        mFragments.add(TabLatestProjectFragment.getInstance())
        mFragments.add(TabSystemFragment.getInstance())
        mFragments.add(TabNavigationFragment.getInstance())
    }

    override fun initWidget() {
        navigation.setNavigationItemSelectedListener(this)
        initViewPager()
        initUserHead()
        resetNvHeader()
        ibt_search.setOnClickListener {
            // 跳转搜索
            SearchActivity.launch(this@MainActivity)
        }
        //根据是否已登录显示和隐藏退出登录按钮
        navigation.menu.findItem(R.id.nv_logout).isVisible = mIsLogin
    }

    private fun initViewPager() {
        val vpAdapter = BaseFragmentPagerAdapter(supportFragmentManager, mFragments, mTitles)
        vp.adapter = vpAdapter
        tabLayout.setupWithViewPager(vp)
    }

    private fun initUserHead() {
        val headerView = navigation.getHeaderView(0)
        val circleTextImageView = headerView.findViewById(R.id.civ_user) as CircleTextImageView
        GlideApp
            .with(this)
            .load(Constant.imgUrls[1])
            .override(800, 800)
            .into(circleTextImageView)
    }

    private fun resetNvHeader() {
        StatusBarUtil.setPaddingSmart(this, ll_title)
        StatusBarUtil.setPaddingSmart(this, navigation)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //todo navigation菜单点击事件


        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(MainActivity::class.java)
                .launch()
        }
    }
}
