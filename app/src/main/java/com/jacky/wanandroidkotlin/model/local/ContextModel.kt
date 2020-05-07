package com.jacky.wanandroidkotlin.model.local

import android.app.Application
import android.content.Context
import com.jacky.wanandroidkotlin.app.ApplicationKit
import io.reactivex.Observable

/**
 * @author:Hzj
 * @date  :2018/12/20/020
 * desc  ：提供applicationContext
 * record：
 */
object ContextModel {

    /**
     * 获取Context
     */
    fun getApplicationContext(): Context {
        return ApplicationKit.instance.application as Context
    }

    fun getApplicationContextObservable(): Observable<Application> {
        val application = getApplicationContext() as Application
        return Observable.just(application)
    }
}