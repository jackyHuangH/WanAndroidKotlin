package com.jacky.wanandroidkotlin.wrapper

import android.view.View

/**
 * @author:Hzj
 * @date  :2020/12/21
 * desc  ：View相关扩展函数
 * record：
 */

/**
 * 快捷子View操作
 */
fun <T : View> View.childViewExt(viewId: Int, extra: T.() -> Unit) {
    findViewById<T>(viewId).apply(extra)
}