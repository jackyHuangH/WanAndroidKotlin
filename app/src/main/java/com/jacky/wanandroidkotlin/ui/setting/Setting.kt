package com.jacky.wanandroidkotlin.ui.setting

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.util.LanguageUtils
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.util.setOnAntiShakeClickListener
import com.zenchn.support.router.Router
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * @author:Hzj
 * @date  :2020/5/8
 * desc  ： 设置
 * record：
 */
class SettingActivity : BaseActivity() {

    private var mThemeSetting by PreferenceUtil<Int>(PreferenceUtil.KEY_SETTING_THEME, 0)
    private var mLanguageSetting by PreferenceUtil<Int>(PreferenceUtil.KEY_SETTING_LANGUAGE, 0)

    override fun getLayoutId(): Int = R.layout.activity_setting

    override fun initWidget() {
        toolbar.apply {
            setNavigationOnClickListener { onBackPressed() }
            title = getString(R.string.setting_title)
        }
        tv_language.setOnAntiShakeClickListener {
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
        tv_theme.setOnAntiShakeClickListener {
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