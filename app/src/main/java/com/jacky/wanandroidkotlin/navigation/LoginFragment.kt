package com.jacky.wanandroidkotlin.navigation

/**
 * @author:Hzj
 * @date  :2020/7/28
 * desc  ：
 * record：
 */
import androidx.navigation.Navigation
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.ui.login.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : BaseVMFragment<LoginViewModel>() {

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun initWidget() {
        //获取携带过来的参数 name,若没有传递值，则使用nav中定义的默认值
        arguments?.getString("name")?.apply {
            btn_login.text = this
        }
        btn_back.setOnClickListener {
            //返回上一个fragment
            Navigation.findNavController(view!!).navigateUp()
        }
    }

    override val startObserve: LoginViewModel.() -> Unit = {

    }

}
