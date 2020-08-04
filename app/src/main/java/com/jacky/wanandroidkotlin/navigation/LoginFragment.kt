package com.jacky.wanandroidkotlin.navigation

/**
 * @author:Hzj
 * @date  :2020/7/28
 * desc  ：
 * record：
 */
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.ui.login.LoginViewModel


class LoginFragment : BaseVMFragment<LoginViewModel>() {

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun initWidget() {

    }

    override val startObserve: LoginViewModel.() -> Unit = {

    }

}
