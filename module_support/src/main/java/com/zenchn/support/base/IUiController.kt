package com.zenchn.support.base

import androidx.annotation.StringRes

/**
 * 作   者： by Hzj on 2017/12/13/013.
 * 描   述：
 * 修订记录：
 */
interface IUiController {
    fun showProgress()
    fun showProgress(msg: CharSequence?)
    fun hideProgress()
    fun showMessage(msg: CharSequence)
    fun showResMessage(@StringRes resId: Int)
}