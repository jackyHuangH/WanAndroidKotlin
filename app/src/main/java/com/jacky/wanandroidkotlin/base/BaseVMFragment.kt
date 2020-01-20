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
abstract class BaseVMFragment<VM : BaseViewModel> : BaseFragment(),IVMView<VM> {
    override lateinit var mViewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewModel()
        super.onViewCreated(view, savedInstanceState)
        startObserve()
    }

    //初始化ViewModel绑定
    private fun initViewModel() {
        provideViewModelClass()?.let {
            mViewModel = ViewModelProviders.of(this).get(it)
            mViewModel.let(lifecycle::addObserver)
        }
    }
}