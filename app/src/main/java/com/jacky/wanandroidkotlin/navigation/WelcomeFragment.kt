package com.jacky.wanandroidkotlin.navigation

/**
 * @author:Hzj
 * @date  :2020/7/28
 * desc  ：
 * record：
 */
import android.app.Application
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.base.BaseViewModel

class WelcomeFragment : BaseVMFragment<WelcomeViewModel>() {

    override fun getLayoutId(): Int = R.layout.fragment_welcome

    override fun initWidget() {

    }

    override val startObserve: WelcomeViewModel.() -> Unit = {

    }

}

class WelcomeViewModel(application: Application) : BaseViewModel(application) {

}