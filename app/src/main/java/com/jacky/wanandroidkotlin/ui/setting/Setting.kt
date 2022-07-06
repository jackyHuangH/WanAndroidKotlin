package com.jacky.wanandroidkotlin.ui.setting

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.common.MMkvKey
import com.jacky.wanandroidkotlin.databinding.ActivitySettingBinding
import com.jacky.wanandroidkotlin.util.ApkUtils
import com.jacky.wanandroidkotlin.util.LanguageUtils
import com.jacky.wanandroidkotlin.util.MMKVUtils
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.wrapper.setOnAntiShakeClickListener
import com.zenchn.support.router.Router

/**
 * @author:Hzj
 * @date  :2020/5/8
 * desc  ： 设置
 * record：
 */
class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    private var mThemeSetting by PreferenceUtil<Int>(PreferenceUtil.KEY_SETTING_THEME, 0)
    private var mLanguageSetting by PreferenceUtil<Int>(PreferenceUtil.KEY_SETTING_LANGUAGE, 0)

    override fun getLayoutId(): Int = R.layout.activity_setting

    override fun initWidget() {
        mViewBinding.includeToolbar.toolbar.apply {
            setNavigationOnClickListener { onBackPressed() }
            title = getString(R.string.setting_title)
        }
        mViewBinding.tvLanguage.setOnAntiShakeClickListener {
            //多语言设置
            MaterialDialog(this).show {
                listItemsSingleChoice(
                    R.array.language_setting_array,
                    initialSelection = mLanguageSetting
                ) { _, index, _ ->
                    LanguageUtils.applyLanguage(this@SettingActivity, index, true)
                }
            }
        }
        mViewBinding.tvTheme.setOnAntiShakeClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //主题模式设置
                MaterialDialog(this).show {
                    listItemsSingleChoice(
                        R.array.theme_setting_array,
                        initialSelection = mThemeSetting
                    ) { _, index, _ ->
                        setAppThemeMode(index)
                        mThemeSetting = index
                    }
                }
            } else {
                showMessage(getString(R.string.setting_theme_not_support))
            }
        }
        //获取保存的logo配置
        val savedLogo = MMKVUtils[MMkvKey.ICON_SETTING, 0] as Int
        mViewBinding.tvChangeIcon.setOnAntiShakeClickListener {
            //启动图标动态切换
            MaterialDialog(this).show {
                listItemsSingleChoice(
                    R.array.icon_setting_array,
                    initialSelection = savedLogo
                ) { _, index, _ ->
                    //动态设置logo
                    changeLogo(index)
                    MMKVUtils.put(MMkvKey.ICON_SETTING, index)
                }
            }
        }
    }

    /**
     * 动态更换logo：在Manifest文件中使用标签准备多个Activity入口，没个activity都指向入口Activity，
     * 并且为每个拥有标签的activity设置单独的icon和应用名，最后调用SystemService 服务kill掉launcher，并执行launcher的重启操作
     * 链接：https://juejin.cn/post/7115413271946985480
     *
     */
    private fun changeLogo(index: Int) {
        //字符串需要和AndroidManifest.xml文件中的<activity-alias>的name相对应
        val defCls = "com.jacky.wanandroidkotlin.ui.splash.SplashActivity"
        val rocketCls = "com.jacky.wanandroidkotlin.rocketLogo"//或者配置成（官方推荐，manifest activity-alias name也要改） com.jacky.wanandroidkotlin.ui.splash.RocketSplashActivity
        val oldClsName = if (index == 0) rocketCls else defCls
        val newClsName = if (index == 0) defCls else rocketCls
        val pm = application.packageManager
        //禁用当前componentName
        pm.setComponentEnabledSetting(
            ComponentName(this, oldClsName),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        //启动新componentName
        pm.setComponentEnabledSetting(
            ComponentName(this, newClsName),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
        reStartApp(pm)
    }

    private fun reStartApp(pm: PackageManager) {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfos) {
            if (resolveInfo.activityInfo != null) {
                am.killBackgroundProcesses(resolveInfo.activityInfo.packageName)
            }
        }
    }

    private fun setAppThemeMode(selected: Int) = AppCompatDelegate.setDefaultNightMode(
        when (selected) {
            0 -> MODE_NIGHT_NO
            1 -> MODE_NIGHT_YES
            2 -> MODE_NIGHT_AUTO_BATTERY
            else -> MODE_NIGHT_FOLLOW_SYSTEM
        }
    )

    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(SettingActivity::class.java)
                .launch()
        }
    }
}