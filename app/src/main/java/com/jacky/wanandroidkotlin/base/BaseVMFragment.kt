package com.jacky.wanandroidkotlin.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

/**
 * @author:Hzj
 * @date  :2019/6/28/028
 * desc  ：ViewModel封装基类Fragment
 * record：
 */
abstract class BaseVMFragment<VM : BaseViewModel> : BaseFragment(), IVMView<VM> {
    override lateinit var mViewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    //初始化ViewModel绑定
    private fun initViewModel() {
        provideViewModelClass()?.let { clazz ->
            mViewModel = ViewModelProviders.of(this).get(clazz).apply {
                mErrorMsg.observe(this@BaseVMFragment, Observer { showMessage(msg = it) })
                mShowLoadingProgress.observe(this@BaseVMFragment, Observer { show ->
                    if (show) showProgress() else hideProgress()
                })
            }
        }
    }

    override fun initLifecycleObserver() {
        super.initLifecycleObserver()
        mViewModel.let(lifecycle::addObserver)
    }
}