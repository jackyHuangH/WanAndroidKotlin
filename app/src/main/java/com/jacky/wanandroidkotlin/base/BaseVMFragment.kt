package com.jacky.wanandroidkotlin.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

/**
 * @author:Hzj
 * @date  :2019/6/28/028
 * desc  ：ViewModel封装基类Fragment
 * record：
 */
abstract class BaseVMFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFragment<VB>(),
    IVMView<VM> {
    override lateinit var mViewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewModel()
        super.onViewCreated(view, savedInstanceState)
        //初始化ViewModel绑定
        startObserve.invoke(mViewModel)
    }

    //初始化ViewModel绑定
    private fun initViewModel() {
        provideViewModelClass(1)?.let { clazz ->
            //注意这里第一个参数owner，传当前页面，fragment
            mViewModel = ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            ).get(clazz).apply {
                mErrorMsg.observe(viewLifecycleOwner, Observer { showMessage(msg = it) })
                mShowLoadingProgress.observe(viewLifecycleOwner, Observer { show ->
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