package com.zenchn.support.base

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.zenchn.support.permission.IPermission

/**
 * 作   者： by Hzj on 2017/12/13/013.
 * 描   述：
 * 修订记录：
 */
interface IActivity : IUiController, IPermission {
    @LayoutRes
    fun getLayoutId(): Int

    fun initWidget()
}

interface IUiController {
    fun showProgress()
    fun showProgress(msg: CharSequence?)
    fun hideProgress()
    fun showMessage(msg: CharSequence)
    fun showResMessage(@StringRes resId: Int)
}