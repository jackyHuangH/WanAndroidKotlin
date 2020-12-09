package com.zenchn.support

import android.os.Environment

/**
 * 描    述：
 * 修订记录：
 *
 * @author hzj
 */
object SupportConfig {
    const val DEFAULT_TAG = "wanandroid"

    // #crash 是否收集报错日志
    const val isReport = true
    @JvmField
    val FILE_PATH = Environment.getExternalStorageDirectory().path + "/wanandroid/library/log/"
    const val FILE_NAME_PREFIX = "crash"
    const val FILE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val FILE_NAME_SUFFIX = ".log"
}