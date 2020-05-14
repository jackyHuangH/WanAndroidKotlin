package com.jacky.wanandroidkotlin.base

import androidx.lifecycle.ViewModel
import com.zenchn.support.base.IActivity
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

//反射获取ViewModel实例
@Suppress("UNCHECKED_CAST")
fun <T : ViewModel> IVMView<T>.provideViewModelClass(): Class<T>? {
    return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as? Class<T>
}