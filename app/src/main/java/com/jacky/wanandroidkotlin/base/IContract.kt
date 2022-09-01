package com.jacky.wanandroidkotlin.base

import androidx.lifecycle.ViewModel
import com.jacky.support.base.IActivity
import java.lang.reflect.ParameterizedType

/**
 * @author:Hzj
 * @date  :2018/10/30/030
 * desc  ：
 * record：
 */
interface IView : IActivity {
    fun onApiFailure(msg: String)

    fun onApiGrantRefuse()
}

interface IVMView<VM : ViewModel> {
    val mViewModel: VM

    val startObserve: (VM.() -> Unit)
}

/**
 *反射获取ViewModel实例
 * @param index viewModel泛型所在索引
 */
@Suppress("UNCHECKED_CAST")
fun <T : ViewModel> IVMView<T>.provideViewModelClass(index: Int = 0): Class<T>? {
    return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[index] as? Class<T>
}