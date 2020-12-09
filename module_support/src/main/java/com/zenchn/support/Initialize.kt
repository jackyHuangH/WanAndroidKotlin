package com.zenchn.support

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.hjq.toast.ToastUtils
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk

class CommonInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        context.let { ModuleManager.init(it) }
        Log.d("Init", "CommonInitializer-create")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

internal object ModuleManager {

    fun init(context: Context) {
        (context as? Application)?.let { application ->
            //初始化tbs
            initX5Preload(application)
            //初始化ToastUtils
            ToastUtils.init(application)
        }
    }

    /**
     * 配置X5内核预加载
     */
    private fun initX5Preload(application: Application) {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        val preCallback = object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
            }

            override fun onViewInitFinished(arg0: Boolean) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("MyApplication", " onViewInitFinished is " + arg0);
            }
        }
        //x5内核初始化接口
        QbSdk.initX5Environment(application, preCallback)
    }
}
