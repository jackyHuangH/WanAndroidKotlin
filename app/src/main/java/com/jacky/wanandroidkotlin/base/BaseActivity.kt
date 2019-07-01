package com.jacky.wanandroidkotlin.base

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.OnKeyboardListener
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.app.ApplicationKit
import com.zenchn.support.base.AbstractAppCompatActivity
import com.zenchn.support.base.IActivityLifecycle
import com.zenchn.support.base.IUiController
import com.zenchn.support.dafault.DefaultActivityLifecycle
import com.zenchn.support.kit.AndroidKit
import com.zenchn.support.utils.StringUtils

/**
 * @author:Hzj
 * @date  :2018/10/30/030
 * desc  ：
 * record：
 */
abstract class BaseActivity : AbstractAppCompatActivity(), IView {
    protected val PAGE_SIZE = 10
    protected lateinit var mImmersionBar: ImmersionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initInstanceState(savedInstanceState)
        initWidget()
        initStatusBar()
    }

    //界面布局的初始化操作
    protected fun initInstanceState(savedInstanceState: Bundle?) {
    }

    protected open fun initStatusBar() {
        mImmersionBar = ImmersionBar.with(this)
        mImmersionBar
            .fitsSystemWindows(true)
            .statusBarColor(R.color.colorPrimary)
            .statusBarDarkFont(false)
        //是否需要监听键盘
        if (addOnKeyboardListener() != null) {
            mImmersionBar
                .keyboardEnable(true)
                //单独指定软键盘模式
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                .setOnKeyboardListener(addOnKeyboardListener())
        }
        mImmersionBar.init()
    }

    protected open fun addOnKeyboardListener(): OnKeyboardListener? {
        return null
    }

    public override fun getDefaultUiController(): IUiController {
        return object : CustomUiController(this) {
            override fun getSnackBarParentView(): View {
                return findViewById(getSnackBarParentIdRes())
            }
        }
    }

    @IdRes
    protected fun getSnackBarParentIdRes(): Int {
        return android.R.id.content
    }

    override fun getDefaultActivityLifecycle(): IActivityLifecycle? {
        return DefaultActivityLifecycle.getInstance()
    }

    override fun onApiFailure(msg: String) {
        showMessage(msg)
    }

    override fun onApiGrantRefuse() {
        ApplicationKit.instance.navigateToLogin(true)
    }

    override fun showMessage(@NonNull msg: CharSequence) {
        if (StringUtils.isNonNull(msg)) {
            super.showMessage(msg)
        }
    }

    override fun onPause() {
        AndroidKit.Keyboard.hideSoftInput(this)
        super.onPause()
    }
}