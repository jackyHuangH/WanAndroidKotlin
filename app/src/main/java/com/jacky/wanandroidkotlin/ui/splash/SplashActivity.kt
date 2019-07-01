package com.jacky.wanandroidkotlin.ui.splash

import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.ui.main.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：闪屏页
 * record：
 */
class SplashActivity : BaseActivity() {
    override fun getLayoutRes(): Int = 0

    override fun initWidget() {
        GlobalScope.launch {
            delay(3000L)
            MainActivity.launch(this@SplashActivity)
            finish()
        }
    }
}