package com.jacky.wanandroidkotlin.base

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

/**
 * @author:Hzj
 * @date  :2018/10/30/030
 * desc  ：ViewModel封装基类Activity
 * record：
 */
abstract class BaseVMActivity<VM : BaseViewModel> : BaseActivity(), IVMView<VM> {

    override lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        initViewModel()
        super.onCreate(savedInstanceState)
    }

    //初始化ViewModel绑定
    private fun initViewModel() {
        provideViewModelClass()?.let { clazz ->
            mViewModel =
                ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(clazz).apply {
                    mErrorMsg.observe(this@BaseVMActivity, Observer { showMessage(msg = it) })
                    mShowLoadingProgress.observe(this@BaseVMActivity, Observer { show ->
                        if (show) showProgress() else hideProgress()
                    })
                }
        }
    }

    override fun initLifecycleObserver() {
        super.initLifecycleObserver()
        mViewModel.let(lifecycle::addObserver)
    }

    override fun onApiFailure(msg: String) {
        hideProgress()
        super.onApiFailure(msg)
    }
}