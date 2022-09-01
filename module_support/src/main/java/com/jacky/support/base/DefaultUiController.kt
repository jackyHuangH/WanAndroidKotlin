package com.jacky.support.base

import android.content.Context
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.hjq.toast.ToastUtils
import com.jacky.support.R
import com.jacky.support.utils.AndroidKit


/**
 * 作    者：hzj on 2019/7/3 14:56
 * 描    述：引入<p>https://github.com/afollestad/material-dialogs</p> kotlin版本
 * 修订记录：
 */

open class DefaultUiController(
    protected var mContext: Context,
    private val mLifecycleOwner: LifecycleOwner
) : IUiController {
    private var mMaterialDialog: MaterialDialog? = null

    /**
     * 显示默认 加载进度条：正在加载数据...
     */
    override fun showProgress() {
        showProgress(mContext.getString(R.string.common_library_loading))
    }

    /**
     * 显示自定义内容加载条
     *
     * @param msg
     */
    override fun showProgress(msg: CharSequence?) {
        mMaterialDialog = MaterialDialog(mContext).customView(R.layout.dialog_loading_layout)
        val customView = mMaterialDialog?.getCustomView()
        // Use the view instance, e.g. to set values or setup listeners
        val tvLoadingMsg = customView?.findViewById(R.id.tv_loading_msg) as TextView
        tvLoadingMsg.text = msg
        mMaterialDialog?.show {
            maxWidth(literal = AndroidKit.Dimens.dp2px(120))
            cancelable(false)
            cancelOnTouchOutside(false)
            lifecycleOwner(mLifecycleOwner)
        }
    }

    override fun hideProgress() {
        mMaterialDialog?.dismiss()
    }

    override fun showMessage(message: CharSequence) {
        ToastUtils.show(message)
    }

    override fun showResMessage(@StringRes resId: Int) {
        showMessage(mContext.getString(resId))
    }
}
