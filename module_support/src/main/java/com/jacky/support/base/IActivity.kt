package com.jacky.support.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.jacky.support.permission.IPermission

/**
 * 作   者： by Hzj on 2017/12/13/013.
 * 描   述：
 * 修订记录：
 */
interface IActivity : IUiController, IPermission {
    @LayoutRes
    fun getLayoutId(): Int

    fun initWidget()

    fun <V : View> findViewWithId(viewId: Int): V
}

interface IUiController {
    fun showProgress()
    fun showProgress(msg: CharSequence?)
    fun hideProgress()
    fun showMessage(msg: CharSequence)
    fun showResMessage(@StringRes resId: Int)
}