package com.jacky.wanandroidkotlin.base

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

/**
 * @author:Hzj
 * @date  :2018/10/30/030
 * desc  ：ViewModel封装基类Activity
 * record：
 */
abstract class BaseVMActivity<VB : ViewBinding, VM : BaseViewModel> : BaseActivity<VB>(), IVMView<VM> {

    override lateinit var mViewModel: VM
    //不使用泛型，官方扩展方式延时初始化方式：
//    val viewModel by viewModels<XXViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        initViewModel()
        super.onCreate(savedInstanceState)
        //初始化ViewModel绑定
        startObserve.invoke(mViewModel)
    }

    //初始化ViewModel绑定
    private fun initViewModel() {
        provideViewModelClass(1)?.let { clazz ->
            mViewModel =
                ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
                    clazz
                ).apply {
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