package com.jacky.wanandroidkotlin.base

import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.OnKeyboardListener
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.app.ApplicationKit
import com.jacky.wanandroidkotlin.app.GlobalLifecycleObserver
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrInterface
import com.zenchn.support.base.DefaultUiController
import com.zenchn.support.base.IUiController
import com.zenchn.support.utils.AndroidKit

/**
 * @author:Hzj
 * @date  :2018/10/30/030
 * desc  ：
 * record：
 */
abstract class BaseActivity : AppCompatActivity(), IView {

    protected lateinit var mImmersionBar: ImmersionBar
    protected var instanceState: Bundle? = null
    protected lateinit var slidrInterface: SlidrInterface
    protected val mUiDelegate: IUiController by lazy {
        DefaultUiController(
            this,
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewInstanceState(savedInstanceState)
        getLayoutRes().takeIf { it > 0 }?.let { setContentView(it) }
        //滑动返回
        slidrInterface = Slidr.attach(this)
        initWidget()
        initStatusBar()
        initLifecycleObserver()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        onNewInstanceState(savedInstanceState)
    }

    @CallSuper
    protected open fun onNewInstanceState(savedInstanceState: Bundle?) {
        this.instanceState = savedInstanceState
    }

    @CallSuper
    open fun initLifecycleObserver() {
        lifecycle.addObserver(GlobalLifecycleObserver.INSTANCE)
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

    override fun onApiFailure(msg: String) {
        showMessage(msg)
    }

    override fun onApiGrantRefuse() {
        ApplicationKit.instance.navigateToLogin(true)
    }

    override fun onPause() {
        AndroidKit.Keyboard.hideSoftInput(this)
        super.onPause()
    }

    override fun showProgress() {
        mUiDelegate.showProgress()
    }

    override fun showProgress(msg: CharSequence?) {
        mUiDelegate.showProgress(msg)
    }

    override fun hideProgress() {
        mUiDelegate.hideProgress()
    }

    override fun showMessage(msg: CharSequence) {
        mUiDelegate.showMessage(msg)
    }

    override fun showResMessage(resId: Int) {
        mUiDelegate.showResMessage(resId)
    }
}