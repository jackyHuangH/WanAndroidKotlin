package com.jacky.wanandroidkotlin.ui.splash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.main.MainActivity
import com.jacky.wanandroidkotlin.wrapper.musicplay.MusicPlayManager
import com.zenchn.support.router.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * @author:Hzj
 * @date  :2020/5/15
 * desc  ：app初始化页面
 * record：
 */
class StartActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MusicPlayManager.initPlayer(this@StartActivity)
        //如果有自动登录功能就替换此处跳转逻辑
        MainActivity.launch(this@StartActivity)
        finish()
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
        finish()
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