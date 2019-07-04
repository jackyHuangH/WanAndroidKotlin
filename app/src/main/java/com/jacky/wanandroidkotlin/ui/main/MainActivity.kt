package com.jacky.wanandroidkotlin.ui.main

import android.app.Activity
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.common.TEST_IMG_URLS
import com.jacky.wanandroidkotlin.common.TOOL_URL
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.UserEntity
import com.jacky.wanandroidkotlin.ui.adapter.BaseFragmentPagerAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.fragmentwrap.FragmentWrapActivity
import com.jacky.wanandroidkotlin.ui.search.SearchActivity
import com.jacky.wanandroidkotlin.ui.tabhome.TabHomeFragment
import com.jacky.wanandroidkotlin.ui.tablatestproject.TabLatestProjectFragment
import com.jacky.wanandroidkotlin.ui.tabnavigation.TabNavigationFragment
import com.jacky.wanandroidkotlin.ui.tabsystem.TabSystemFragment
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.util.StatusBarUtil
import com.jacky.wanandroidkotlin.wrapper.DialogProvider
import com.jacky.wanandroidkotlin.wrapper.glide.GlideApp
import com.zenchn.support.router.Router
import com.zenchn.support.widget.CircleTextImageView
import kotlinx.android.synthetic.main.activity_main.*


/**
 * 首页
 */
class MainActivity : BaseVMActivity<MainViewModel>(), NavigationView.OnNavigationItemSelectedListener {

    private val mTitles = arrayListOf("首页", "最新项目", "体系", "导航")
    private var mFragments: ArrayList<Fragment> = ArrayList()

    private var mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)
    private var mUserInfo by PreferenceUtil(PreferenceUtil.KEY_USER_INFO, "")
    private lateinit var mTvUserName: TextView

    override fun getLayoutRes() = R.layout.activity_main

    init {
        mFragments.add(TabHomeFragment.getInstance())
        mFragments.add(TabLatestProjectFragment.getInstance())
        mFragments.add(TabSystemFragment.getInstance())
        mFragments.add(TabNavigationFragment.getInstance())
    }

    override fun provideViewModelClass(): Class<MainViewModel>? = MainViewModel::class.java

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
        val vpAdapter =
            BaseFragmentPagerAdapter(supportFragmentManager, mFragments, mTitles)
        vp.adapter = vpAdapter
        tabLayout.setupWithViewPager(vp)
    }

    private fun initUserHead() {
        val headerView = navigation.getHeaderView(0)
        val circleTextImageView = headerView.findViewById(R.id.civ_user) as CircleTextImageView
        mTvUserName = headerView.findViewById(R.id.tv_username) as TextView
        if (mIsLogin && mUserInfo.isNotEmpty()) {
            val userEntity = Gson().fromJson(mUserInfo, UserEntity::class.java)
            mTvUserName.text = userEntity.username
        }
        GlideApp
            .with(this)
            .load(TEST_IMG_URLS[1])
            .override(800, 800)
            .into(circleTextImageView)
    }

    private fun resetNvHeader() {
        StatusBarUtil.setPaddingSmart(this, ll_title)
        StatusBarUtil.setPaddingSmart(this, navigation)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //todo navigation菜单点击事件
        when (item.itemId) {
            R.id.nv_blog -> {
                //公众号
                goToFragmentWrap(true)
            }
            R.id.nv_type -> {
                //项目分类
                goToFragmentWrap(false)
            }
            R.id.nv_util -> {
                //工具
                BrowserActivity.launch(this@MainActivity, TOOL_URL)
            }
            R.id.nv_collection -> {
                //收藏
            }
            R.id.nv_about -> {
                //关于
            }
            R.id.nv_logout -> {
                DialogProvider.showSimpleDialog(this@MainActivity,
                    "确定退出登录吗？", object : DialogProvider.ConfirmCallback {
                        override fun onConfirm() {
                            //退出登录
                            mViewModel.logout()
                        }
                    })
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun goToFragmentWrap(isBlog: Boolean) {
        FragmentWrapActivity.launch(this@MainActivity, isBlog)
    }

    override fun startObserve() {
        mViewModel.apply {
            mLogoutInfo.observe(this@MainActivity, Observer {
                //退出登录成功
                it.run {
                    showMessage(it)
                    navigation.menu.findItem(R.id.nv_logout).isVisible = false
                    //清除用户信息和登录信息缓存
                    mIsLogin = false
                    mUserInfo = ""
                    WanRetrofitClient.mCookieJar.clear()
                    mTvUserName.text = getString(R.string.name_default)
                }
            })
            mErrorMsg.observe(this@MainActivity, Observer {
                showMessage(it)
            })
        }
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
