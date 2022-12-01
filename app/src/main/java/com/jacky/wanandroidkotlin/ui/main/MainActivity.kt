package com.jacky.wanandroidkotlin.ui.main

import android.app.Activity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.amitshekhar.DebugDB
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.jacky.support.managers.FragmentSwitchHelper
import com.jacky.support.router.Router
import com.jacky.support.utils.LoggerKit
import com.jacky.support.widget.CircleTextImageView
import com.jacky.wanandroidkotlin.BuildConfig
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.app.GlobalLifecycleObserver
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.common.TEST_IMG_URLS
import com.jacky.wanandroidkotlin.common.TOOL_URL
import com.jacky.wanandroidkotlin.databinding.ActivityMainBinding
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.UserEntity
import com.jacky.wanandroidkotlin.test.CustomViewActivity
import com.jacky.wanandroidkotlin.test.PuzzleActivity
import com.jacky.wanandroidkotlin.test.TestActivity
import com.jacky.wanandroidkotlin.ui.about.AboutActivity
import com.jacky.wanandroidkotlin.ui.adapter.BaseViewPager2Adapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.fragmentwrap.FragmentWrapActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.mycollect.MyCollectActivity
import com.jacky.wanandroidkotlin.ui.search.SearchActivity
import com.jacky.wanandroidkotlin.ui.setting.SettingActivity
import com.jacky.wanandroidkotlin.ui.tabhome.TabHomeFragment
import com.jacky.wanandroidkotlin.ui.tablatestproject.TabLatestProjectFragment
import com.jacky.wanandroidkotlin.ui.tabnavigation.TabNavigationFragment
import com.jacky.wanandroidkotlin.ui.tabsystem.TabSystemFragment
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.util.StatusBarUtil
import com.jacky.wanandroidkotlin.util.setOnAntiShakeClickListener
import com.jacky.wanandroidkotlin.wrapper.DialogProvider
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.viewClickListener
import kotlin.random.Random


/**
 * 首页
 */
class MainActivity : BaseVMActivity<ActivityMainBinding, MainViewModel>(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mTvUserName: TextView
    private val mTitles by lazy {
        arrayListOf(
            getString(R.string.main_tab_home),
            getString(R.string.main_tab_latest_project),
            getString(R.string.main_tab_system),
            getString(R.string.main_tab_nav)
        )
    }
    private var mFragments: ArrayList<Fragment> = ArrayList()

    private var mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)
    private var mUserInfo by PreferenceUtil(PreferenceUtil.KEY_USER_INFO, "")

    override fun getLayoutId() = R.layout.activity_main

    init {
        mFragments.apply {
            add(TabHomeFragment.getInstance())
            add(TabLatestProjectFragment.getInstance(0, true))
            add(TabSystemFragment.getInstance())
            add(TabNavigationFragment.getInstance())
        }
    }

    override fun initWidget() {
        mViewBinding.navigation.setNavigationItemSelectedListener(this)
        initBottomNav()
//        initViewPager()
        initUserHead()
        resetNvHeader()
        viewClickListener(R.id.ibt_search) {
            // 跳转搜索
            SearchActivity.launch(this@MainActivity)
        }
        viewClickListener(R.id.ibt_menu) {
            if (mViewBinding.drawerLayout.isOpen) mViewBinding.drawerLayout.close() else mViewBinding.drawerLayout.open()
        }
        //根据是否已登录显示和隐藏退出登录按钮
        mViewBinding.navigation.menu.findItem(R.id.nv_logout).isVisible = mIsLogin
        //调试模式显示test模块
        mViewBinding.navigation.menu.findItem(R.id.nv_test).isVisible = BuildConfig.DEBUG
        initDrawerListener()
        //检测屏幕刷新率
//        RateUtil.detectRefreshRate()
        LoggerKit.d("debug db:${DebugDB.getAddressLog()}")
    }

    //A to B --> pair<A,B>
    private val tabs = arrayOf(
        0 to "首页",
        1 to "商场",
        2 to "我的",
    )

    private fun initBottomNav() {
        val fragmentSwitchHelper = FragmentSwitchHelper(supportFragmentManager, R.id.fl_container)
        //默认显示首页
        fragmentSwitchHelper.add(mFragments[0])
        mViewBinding.tvTitle.text = mTitles[0]
        mViewBinding.bottomBar.onItemSelected = { pos ->
            mViewBinding.tvTitle.text = mTitles[pos]
            //切换fragment
            val targetFragment = mFragments[pos]
            fragmentSwitchHelper.switchFragment(targetFragment)
        }
    }

    /**
     * viewpager2形式首页
     */
    private fun initViewPager() {
        val vpAdapter = BaseViewPager2Adapter(supportFragmentManager, lifecycle, mFragments)
        val vp = mViewBinding.vp
        vp.adapter = vpAdapter
        val tablayout = mViewBinding.tabLayout
        //TabLayout绑定viewpager2
        TabLayoutMediator(tablayout, vp) { tab, index ->
            //绑定标题
            tab.text = mTitles[index]
        }.attach()
    }

    private fun initUserHead() {
        val headerView = mViewBinding.navigation.getHeaderView(0)
        val circleTextImageView = headerView.findViewById(R.id.civ_user) as CircleTextImageView
        mTvUserName = headerView.findViewById(R.id.tv_username) as TextView
        if (mIsLogin && mUserInfo.isNotEmpty()) {
            val userEntity = Gson().fromJson(mUserInfo, UserEntity::class.java)
            mTvUserName.text = userEntity.username
        } else {
            mTvUserName.setOnAntiShakeClickListener { LoginActivity.launch(this) }
        }
        Glide
            .with(this)
            .load(TEST_IMG_URLS[Random.nextInt(6)])
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(circleTextImageView)
    }

    private fun resetNvHeader() {
        val llTitle = getView<LinearLayout>(R.id.ll_title)
        StatusBarUtil.setPaddingSmart(this, llTitle)
        StatusBarUtil.setPaddingSmart(this, mViewBinding.navigation)
    }

    private fun initDrawerListener() {
        mViewBinding.drawerLayout.apply {
            addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                override fun onDrawerClosed(drawerView: View) {

                }
            })
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mViewBinding.drawerLayout.apply {
            performDrawerNavigation(item)
            closeDrawer(GravityCompat.START)
        }
        return true
    }

    /**
     * 执行侧栏跳转
     */
    private fun performDrawerNavigation(item: MenuItem) {
        //navigation菜单点击事件
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
                if (mIsLogin) {
                    MyCollectActivity.launch(this@MainActivity)
                } else {
                    LoginActivity.launch(this@MainActivity)
                }
            }
            R.id.nv_setting -> {
                //设置
                SettingActivity.launch(this@MainActivity)
            }
            R.id.nv_about -> {
                //关于
                AboutActivity.launch(this@MainActivity)
            }
            R.id.nv_test -> {
                //test
                TestActivity.launch(this@MainActivity)
            }
            R.id.nv_puzzle -> {
                //拼图游戏
                PuzzleActivity.launch(this@MainActivity)
            }
            R.id.nv_custom_view -> {
                //自定义View
                CustomViewActivity.launch(this@MainActivity)
            }
            R.id.nv_logout -> {
                DialogProvider.showSimpleDialog(this@MainActivity, "确定退出登录吗？") {
                    //退出登录
                    mViewModel.logout()
                }
            }
        }
    }

    private fun goToFragmentWrap(isBlog: Boolean) {
        FragmentWrapActivity.launch(this@MainActivity, isBlog)
    }

    override val startObserve: MainViewModel.() -> Unit = {
        mLogoutInfo.observe(this@MainActivity, Observer {
            //退出登录成功
            it.run {
                showMessage(it)
                mViewBinding.navigation.menu.findItem(R.id.nv_logout).isVisible = false
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

    private var mExitTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis().minus(mExitTime) <= 2000) {
            GlobalLifecycleObserver.INSTANCE.clearActivityStackAndCallback()
            finish()
        } else {
            mExitTime = System.currentTimeMillis()
            showResMessage(R.string.common_click_double_to_exit_app)
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
