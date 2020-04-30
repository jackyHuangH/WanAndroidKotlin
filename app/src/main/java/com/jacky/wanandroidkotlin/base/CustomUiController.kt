package com.jacky.wanandroidkotlin.base

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.zenchn.support.dafault.DefaultUiController
import com.zenchn.support.widget.tips.SuperToast

/**
 * @author:Hzj
 * @date  :2018/10/30/030
 * desc  ：
 * record：
 */
abstract class CustomUiController(context: Context, lifecycleOwner: LifecycleOwner) :
    DefaultUiController(context, lifecycleOwner) {

    override fun showMessage(message: CharSequence) {
        SuperToast.showDefaultMessage(mContext, message.toString())
    }

    override fun showResMessage(resId: Int) {
        showMessage(mContext.getString(resId))
    }

    protected abstract fun getSnackBarParentView(): View
}