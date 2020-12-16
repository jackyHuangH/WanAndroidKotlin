package com.jacky.wanandroidkotlin.app

import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.content.Intent
import android.util.Log
import androidx.annotation.CallSuper
import androidx.multidex.MultiDexApplication
import com.hjq.toast.ToastUtils
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.main.MainActivity
import com.jacky.wanandroidkotlin.util.LanguageUtils
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.zenchn.support.base.ICrashCallback
import com.zenchn.support.crash.DefaultUncaughtHandler


class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        //其他初始化
        ApplicationKit.initKit(this)
        //读取语言配置
        LanguageUtils.attachBaseContext(this)
        //多语言设置初始化
        registerActivityLifecycleCallbacks(LanguageUtils.activityLifecycleCallbacks)
    }
}

/**
 * @author:Hzj
 * @date  :2018/10/30/030
 * desc  ：
 * record：
 */
class ApplicationKit {

    fun initSetting(application: Application?) {
        application?.let {
            clearNotify(it)
            initCrashHandler(it)
        }
    }

    /**
     * 初始化crash异常处理
     */
    @CallSuper
    protected fun initCrashHandler(application: Application) {
        DefaultUncaughtHandler.getInstance().init(application, object : ICrashCallback {
            override fun onCrash(thread: Thread?, ex: Throwable?) {
                GlobalLifecycleObserver.INSTANCE.exitApp()
            }
        })
    }


    /**
     * 清理通知栏
     */
    private fun clearNotify(application: Application) {
        val nm = application.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }

    fun navigateToLogin(grantRefuse: Boolean) {
        if (grantRefuse) {
            ToastUtils.show(R.string.login_error_grant_refused)
        }
        val topActivity = GlobalLifecycleObserver.INSTANCE.getTopActivity()
        if (topActivity != null) {
            LoginActivity.launch(topActivity)
        } else {
            mApplication?.let {
                val intent = Intent(it, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                it.startActivity(intent)
            }
        }
    }

    private object SingletonInstance {
        internal val INSTANCE = ApplicationKit()
    }

    //伴生类，对象为静态单例
    companion object {
        var mApplication: Application? = null

        val instance: ApplicationKit
            get() = SingletonInstance.INSTANCE

        fun initKit(application: Application?) {
            mApplication = application
            instance.initSetting(application)
        }
    }

}