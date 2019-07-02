package com.jacky.wanandroidkotlin.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders

/**
 * @author:Hzj
 * @date  :2019/6/28/028
 * desc  ：ViewModel封装基类Fragment
 * record：
 */
abstract class BaseVMFragment<VM : BaseViewModel> : BaseFragment() {
    protected lateinit var mViewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewModel()
        super.onViewCreated(view, savedInstanceState)
        startObserve()
    }

    abstract fun startObserve()

    //初始化ViewModel绑定
    private fun initViewModel() {
        provideViewModelClass()?.let {
            mViewModel = ViewModelProviders.of(this).get(it)
            mViewModel.let(lifecycle::addObserver)
        }
    }

    abstract fun provideViewModelClass(): Class<VM>?

    override fun onDestroyView() {
        mViewModel.let {
            lifecycle.removeObserver(it)
        }
        super.onDestroyView()
    }
}