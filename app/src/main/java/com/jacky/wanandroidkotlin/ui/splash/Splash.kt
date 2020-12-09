package com.jacky.wanandroidkotlin.ui.splash

import android.content.Intent
import com.jacky.wanandroidkotlin.base.BaseActivity

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：闪屏页
 * record：
 */
class SplashActivity : BaseActivity() {

    override fun getLayoutId(): Int = 0

    override fun initWidget() {
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot) {
            if (intent != null) {
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == intent.action) {
                    finish()
                    return
                }
            }
        }
        //跳转到startActivity
        StartActivity.launch(this)
        finish()
    }
}
