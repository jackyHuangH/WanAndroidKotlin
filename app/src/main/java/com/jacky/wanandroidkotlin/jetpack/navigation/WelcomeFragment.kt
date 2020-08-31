package com.jacky.wanandroidkotlin.jetpack.navigation

/**
 * @author:Hzj
 * @date  :2020/7/28
 * desc  ：
 * record：
 */
import android.app.Application
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.base.BaseViewModel
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : BaseVMFragment<WelcomeViewModel>() {

    override fun getLayoutId(): Int = R.layout.fragment_welcome

    override fun initWidget() {
        btn_login.setOnClickListener {
            //通过Navigation切换Fragment
            //携带参数
            val bundle = Bundle()
//            bundle.putString("name","我是欢迎页传递的参数")
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment, bundle)
        }
        btn_register.setOnClickListener {
            //通过Navigation切换Fragment
            findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
        }
    }

    override val startObserve: WelcomeViewModel.() -> Unit = {

    }

}

class WelcomeViewModel(application: Application) : BaseViewModel(application) {

}