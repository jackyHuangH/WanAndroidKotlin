package com.jacky.wanandroidkotlin.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.jacky.wanandroidkotlin.BuildConfig
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.app.GlobalLifecycleObserver
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.common.TEST_IMG_URLS
import com.jacky.wanandroidkotlin.common.TOOL_URL
import com.jacky.wanandroidkotlin.jetpack.binding.AnimBinding
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.UserEntity
import com.jacky.wanandroidkotlin.test.TestActivity
import com.jacky.wanandroidkotlin.ui.about.AboutActivity
import com.jacky.wanandroidkotlin.ui.adapter.BaseFragmentPagerAdapter
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
import com.jacky.wanandroidkotlin.wrapper.glide.GlideApp
import com.zenchn.support.permission.RequestCode
import com.zenchn.support.permission.applySelfPermissionsStrict
import com.zenchn.support.permission.checkSelfPermission
import com.zenchn.support.router.Router
import com.zenchn.support.widget.CircleTextImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


/**
 * 首页
 */
class MainActivity : BaseVMActivity<MainViewModel>(),
    NavigationView.OnNavigationItemSelectedListener {

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
    private lateinit var mTvUserName: TextView

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
        initPermissions()
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

        navigation.menu.findItem(R.id.nv_test).isVisible = BuildConfig.DEBUG
        initDrawerListener()
    }

    private fun initPermissions() {
        //申请存储权限
        checkSelfPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            onGranted = {},
            onDenied = {
                showMessage("请授予存储权限")
                applySelfPermissionsStrict(Manifest.permission.WRITE_EXTERNAL_STORAGE) {}
            })
        //申请电话权限
        checkSelfPermission(
            Manifest.permission.READ_PHONE_STATE,
            onGranted = { },
            onDenied = {
                showMessage("请授予电话权限")
                applySelfPermissionsStrict(Manifest.permission.READ_PHONE_STATE) {
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.OS_SETTING) {
            initPermissions()
        }
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
        } else {
            mTvUserName.setOnAntiShakeClickListener { LoginActivity.launch(this) }
        }
        GlideApp
            .with(this)
            .load(TEST_IMG_URLS[Random.nextInt(6)])
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(circleTextImageView)
    }

    private fun resetNvHeader() {
        StatusBarUtil.setPaddingSmart(this, ll_title)
        StatusBarUtil.setPaddingSmart(this, navigation)
    }

    private fun initDrawerListener() {
        drawer_layout.apply {
            addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                override fun onDrawerClosed(drawerView: View) {
                    performDrawerNavigation(this@apply)
                }
            })
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.apply {
            tag = item
            closeDrawer(GravityCompat.START)
        }
        return true
    }

    /**
     * 执行侧栏跳转
     */
    private fun performDrawerNavigation(drawerLayout: DrawerLayout) {
        val item = drawerLayout.tag as? MenuItem
        //navigation菜单点击事件
        when (item?.itemId) {
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

    private var mExitTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis().minus(mExitTime) <= 2000) {
            GlobalLifecycleObserver.INSTANCE.exitApp()
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
