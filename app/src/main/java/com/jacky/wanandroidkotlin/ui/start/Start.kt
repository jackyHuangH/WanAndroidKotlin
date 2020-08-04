package com.jacky.wanandroidkotlin.ui.start

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.main.MainActivity
import com.zenchn.support.router.Router
import kotlinx.coroutines.*

/**
 * @author:Hzj
 * @date  :2020/5/15
 * desc  ：app初始化页面
 * record：
 */
class StartActivity : BaseActivity(), CoroutineScope by MainScope() {

    override fun getLayoutId(): Int = 0

    override fun initWidget() {
        //如果有自动登录功能就替换此处跳转逻辑
        launch {
            withContext(Dispatchers.IO) { delay(50L) }
            jumpMainOrLogin(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        jumpMainOrLogin(intent)
    }

    private fun jumpMainOrLogin(intent: Intent?) {
        val goLogin = intent?.getBooleanExtra(KEY_WHETHER_RESTART_LOGIN, false)
        if (goLogin == true) {
            LoginActivity.launch(this@StartActivity)
        } else {
            MainActivity.launch(this@StartActivity)
        }
    }

    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(StartActivity::class.java)
                .launch()
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
            .to(StartActivity::class.java)
            .launch()
    }
}