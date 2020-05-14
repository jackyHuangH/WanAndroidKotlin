package com.jacky.wanandroidkotlin.ui.splash

import android.app.Activity
import android.content.Context
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.main.MainActivity
import com.zenchn.support.router.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：闪屏页
 * record：
 */
class SplashActivity : BaseActivity(), CoroutineScope by MainScope() {
    private val mGoLogin by lazy { intent.getBooleanExtra(KEY_WHETHER_RESTART_LOGIN, false) }

    override fun getLayoutRes(): Int = 0

    override fun initWidget() {
        launch {
            delay(500L)
            if (mGoLogin) {
                LoginActivity.launch(this@SplashActivity)
            } else {
                MainActivity.launch(this@SplashActivity)
            }
            finish()
        }
    }
}

private const val KEY_WHETHER_RESTART_LOGIN = "KEY_WHETHER_RESTART_LOGIN"

fun Context?.navigateToRestart(gotoLogin: Boolean) {
    this?.let {
        Router
            .newInstance()
            .from(it as Activity)
            .putBoolean(KEY_WHETHER_RESTART_LOGIN, gotoLogin)
            .to(SplashActivity::class.java)
            .launch()
    }
}