package com.jacky.wanandroidkotlin.app

import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.content.Intent
import android.util.Log
import androidx.annotation.CallSuper
import com.hjq.toast.ToastUtils
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.main.MainActivity
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.zenchn.support.base.GlobalLifecycleObserver
import com.zenchn.support.base.ICrashCallback
import com.zenchn.support.crash.DefaultUncaughtHandler


/**
 * @author:Hzj
 * @date  :2018/10/30/030
 * desc  ：
 * record：
 */
class ApplicationKit {
    var application: Application? = null

    fun initSetting(application: Application?) {
        application?.let {
            clearNotify(it)
            initX5Preload(it)
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
     * 配置X5内核预加载
     */
    private fun initX5Preload(application: Application) {
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
            application?.let {
                val intent = Intent(application, MainActivity::class.java)
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
        val instance: ApplicationKit
            get() = SingletonInstance.INSTANCE

        fun initKit(application: Application?) {
            instance.application = application
            instance.initSetting(application)
        }
    }

}