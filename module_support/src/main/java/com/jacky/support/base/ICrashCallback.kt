package com.jacky.support.base

/**
 * 作    者：wangr on 2017/5/4 11:28
 * 描    述：
 * 修订记录：
 */
interface ICrashCallback {
    fun onCrash(thread: Thread?, ex: Throwable?)
}