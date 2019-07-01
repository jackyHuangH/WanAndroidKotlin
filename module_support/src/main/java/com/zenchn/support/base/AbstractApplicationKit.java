package com.zenchn.support.base;

import android.app.Application;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import com.zenchn.support.dafault.DefaultUncaughtHandler;


/**
 * 作    者：wangr on 2017/8/26 15:32
 * 描    述：Application管理类
 * 修订记录：
 */
public abstract class AbstractApplicationKit implements IApplicationKit, ICrashCallback {

    protected Application application;

    @Override
    public void initKit(@NonNull Application application) {
        this.application = application;
        initSetting();//设置用户自定义设置
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @CallSuper
    @Override
    public void initSetting() {
        initCrashHandler();
    }

    /**
     * 初始化crash异常处理
     */
    @CallSuper
    protected void initCrashHandler() {
        DefaultUncaughtHandler.getInstance().init(application, this);
    }

}
