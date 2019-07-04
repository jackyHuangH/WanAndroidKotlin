package com.jacky.wanandroidkotlin.app

import android.app.Activity
import android.app.NotificationManager
import android.content.Intent
import android.util.Log
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.ui.main.MainActivity
import com.squareup.leakcanary.LeakCanary
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.zenchn.support.base.AbstractApplicationKit
import com.zenchn.support.base.ActivityLifecycleCallback
import com.zenchn.support.dafault.DefaultActivityLifecycle
import com.zenchn.support.widget.tips.SuperToast


/**
 * @author:Hzj
 * @date  :2018/10/30/030
 * desc  ：
 * record：
 */
class ApplicationKit private constructor() : AbstractApplicationKit(), ActivityLifecycleCallback {

    private val mLazyActivityLifecycle: DefaultActivityLifecycle by lazy {
        DefaultActivityLifecycle.getInstance()
    }

    fun exitApp() {
        mLazyActivityLifecycle.exitApp()
    }

    fun navigateToLogin(grantRefuse: Boolean) {
        if (grantRefuse) {
            SuperToast.showDefaultMessage(
                getApplication(),
                getApplication().getString(R.string.login_error_grant_refused)
            )
        }
        val topActivity = mLazyActivityLifecycle.topActivity
        if (topActivity != null) {
//            LoginActivity
//                    .launch(topActivity)
        } else {
            val intent = Intent(application, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            application.startActivity(intent)
        }
    }

    override fun initSetting() {
        super.initSetting()
        initActivityLifecycle()
        clearNotify()
        initLeakCanary()
        initX5Preload()
    }

    /**
     * 配置X5内核预加载
     */
    private fun initX5Preload() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        val preCallback = object : PreInitCallback {
            override fun onCoreInitFinished() {
            }

            override fun onViewInitFinished(arg0: Boolean) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }
        }
        //x5内核初始化接口
        QbSdk.initX5Environment(application, preCallback)
    }

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(application)) return
        LeakCanary.install(application)
    }

    private fun initActivityLifecycle() {
        mLazyActivityLifecycle.addCallback(this)
    }

    /**
     * 清理通知栏
     */
    private fun clearNotify() {
        val nm = application.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }

    override fun onCrash(thread: Thread?, ex: Throwable?) {
        mLazyActivityLifecycle.exitApp()
    }

    override fun onBackground() {

    }

    override fun onForeground() {

    }

    override fun onDestroyedSelf() {

    }


    private object SingletonInstance {
        internal val INSTANCE = ApplicationKit()
    }

    //伴生类，对象为静态单例
    companion object {
        val instance: ApplicationKit
            get() = SingletonInstance.INSTANCE
    }

}