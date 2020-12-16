package com.jacky.wanandroidkotlin.app

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.zenchn.support.CommonInitializer

/**
 * @author:Hzj
 * @date  :2020/12/1
 * desc  ：startup全局管理所有module初始化
 * record：注意：先执行这里的create,最后执行application的create
 */
class AppGlobalInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        //do initialize

    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(CommonInitializer::class.java)
    }
}