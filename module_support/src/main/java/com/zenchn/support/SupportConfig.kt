package com.zenchn.support;

import android.app.Application;
import android.os.Environment;

import com.hjq.toast.ToastUtils;

/**
 * 描    述：
 * 修订记录：
 *
 * @author hzj
 */
public class SupportConfig {

    public static final String DEFAULT_TAG = "wanandroid";

    // #crash 是否收集报错日志
    public static final boolean isReport = true;
    public static final String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/wanandroid/library/log/";
    public static final String FILE_NAME_PREFIX = "crash";
    public static final String FILE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FILE_NAME_SUFFIX = ".log";

    /**
     * 模块初始化
     */
    public static void init(Application application) {
        ToastUtils.init(application);
    }
}
