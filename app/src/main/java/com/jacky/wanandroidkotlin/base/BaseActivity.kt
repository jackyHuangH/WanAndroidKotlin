package com.jacky.wanandroidkotlin.base

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.OnKeyboardListener
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.app.ApplicationKit
import com.jacky.wanandroidkotlin.app.GlobalLifecycleObserver
import com.jacky.wanandroidkotlin.wrapper.adaptHighRefresh
import com.jacky.wanandroidkotlin.wrapper.createViewBinding
import com.jacky.support.base.DefaultUiController
import com.jacky.support.base.IUiController
import com.jacky.support.utils.AndroidKit

/**
 * @author:Hzj
 * @date  :2018/10/30/030
 * desc  ：baseActivity 基类
 * record：
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), IView {

    protected lateinit var mViewBinding: VB

    protected lateinit var mImmersionBar: ImmersionBar

    private var instanceState: Bundle? = null
    private val mUiDelegate: IUiController by lazy {
        DefaultUiController(
            this,
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //适配屏幕高刷新率
        adaptHighRefresh()
        super.onCreate(savedInstanceState)
        onNewInstanceState(savedInstanceState)
        try {
            mViewBinding = createViewBinding(javaClass, layoutInflater)
                ?: throw IllegalStateException("ViewBinding init fail.")
            mViewBinding.let {
                //                getLayoutId().takeIf { it > 0 }?.let { setContentView(it) }
                setContentView(it.root)
                initWidget()
                initStatusBar()
                initLifecycleObserver()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun <V : View> findViewWithId(viewId: Int): V = findViewById<V>(viewId)

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
            .navigationBarColor(R.color.backgroundColor)
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