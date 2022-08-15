package com.jacky.wanandroidkotlin.app

import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.content.Intent
import androidx.annotation.CallSuper
import androidx.multidex.MultiDexApplication
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.hjq.toast.ToastUtils
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.main.MainActivity
import com.jacky.wanandroidkotlin.util.LanguageUtils
import com.jacky.wanandroidkotlin.util.MMKVUtils
import com.zenchn.support.crash.UncaughtExHandler
import java.net.SocketTimeoutException


class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
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
            MMKVUtils.init(it)
            //init blockcanary
//            BlockCanary.install(it, BlockCanaryContext()).start()
            //百度地图初始化
            //在使用SDK各组件之前初始化context信息，传入ApplicationContext
            SDKInitializer.initialize(it)
            //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
            //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
            SDKInitializer.setCoordType(CoordType.GCJ02)
        }
    }

    /**
     * 初始化crash异常处理
     */
    @CallSuper
    protected fun initCrashHandler(application: Application) {
        UncaughtExHandler.getInstance().init(application) { thread, ex ->
            if (ex is SocketTimeoutException) {
                ToastUtils.show("无法连接服务器")
            } else {
                GlobalLifecycleObserver.INSTANCE.clearActivityStackAndCallback()
                GlobalLifecycleObserver.restartApp(application)
            }
        }
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