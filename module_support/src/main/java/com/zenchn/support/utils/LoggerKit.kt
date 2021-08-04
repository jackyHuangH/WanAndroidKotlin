package com.zenchn.support.utils

import com.orhanobut.logger.*

/**
 * 作    者：hzj
 * 描    述：Logger 2.2.0版本封装
 * 修订记录：
 */
object LoggerKit {
    /**
     * application中调用此初始化方法
     *
     * @param tag
     */
    fun init(tag: String?) {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .tag(tag)
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
        val csvFormatStrategy: FormatStrategy = CsvFormatStrategy.newBuilder()
            .tag(tag)
            .build()
        //是否记录到文件，默认false
//        Logger.addLogAdapter(new DiskLogAdapter(csvFormatStrategy));
    }

    fun log(priority: Int, tag: String?, message: String?, throwable: Throwable?) {
        if (BuildConfig.DEBUG) {
            Logger.log(priority, tag, message, throwable)
        }
    }

    fun d(message: String?, vararg args: Any?) {
        if (BuildConfig.DEBUG) {
            Logger.d(message, *args)
        }
    }

    fun d(`object`: Any?) {
        if (BuildConfig.DEBUG) {
            Logger.d(`object`)
        }
    }

    fun e(throwable: Throwable?, message: String?, vararg args: Any?) {
        if (BuildConfig.DEBUG) {
            Logger.e(throwable, message, *args)
        }
    }

    fun e(message: String?, vararg args: Any?) {
        if (BuildConfig.DEBUG) {
            Logger.e(message, *args)
        }
    }

    fun e(e: Exception, vararg args: Any?) {
        if (BuildConfig.DEBUG) {
            Logger.e(e.toString(), *args)
        }
    }

    fun i(message: String?, vararg args: Any?) {
        if (BuildConfig.DEBUG) {
            Logger.i(message, *args)
        }
    }

    fun v(message: String?, vararg args: Any?) {
        if (BuildConfig.DEBUG) {
            Logger.v(message, *args)
        }
    }

    fun w(message: String?, vararg args: Any?) {
        if (BuildConfig.DEBUG) {
            Logger.w(message, *args)
        }
    }

    fun wtf(message: String?, vararg args: Any?) {
        if (BuildConfig.DEBUG) {
            Logger.wtf(message, *args)
        }
    }

    fun json(json: String?) {
        if (BuildConfig.DEBUG) {
            Logger.json(json)
        }
    }

    fun xml(xml: String?) {
        if (BuildConfig.DEBUG) {
            Logger.xml(xml)
        }
    }
}