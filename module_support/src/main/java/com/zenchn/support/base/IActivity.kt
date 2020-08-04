package com.zenchn.support.base

import androidx.annotation.LayoutRes

/**
 * 作   者： by Hzj on 2017/12/13/013.
 * 描   述：
 * 修订记录：
 */
interface IActivity : IUiController {
    @LayoutRes
    fun getLayoutId(): Int

    fun initWidget()
}