package com.jacky.wanandroidkotlin.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：闪屏页
 * record：
 */
open class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
//        StartActivity.launch(this)
        GuideActivity.launch(this)
        finish()
    }
}

/**
 * 新的logo启动页配置
 */
class RocketSplashActivity : SplashActivity() {

}
