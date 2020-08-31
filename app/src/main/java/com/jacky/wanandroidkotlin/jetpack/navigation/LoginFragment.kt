package com.jacky.wanandroidkotlin.jetpack.navigation

/**
 * @author:Hzj
 * @date  :2020/7/28
 * desc  ：
 * record：
 */
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseFragment
import com.jacky.wanandroidkotlin.databinding.FragmentLoginBinding
import com.jacky.wanandroidkotlin.jetpack.nike.NikeMainActivity
import com.jacky.wanandroidkotlin.wrapper.isNotNullAndNotEmpty
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : BaseFragment() {

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //DataBinding添加绑定
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        val viewModel = LoginModel("", "", context!!)
        binding.model = viewModel
        binding.activity = activity
        return binding.root
    }

    override fun initWidget() {
        //获取携带过来的参数 name,若没有传递值，则使用nav中定义的默认值
        arguments?.getString("name")?.apply {
            btn_login.text = this
        }
        /* btn_back.setOnClickListener {
             //返回上一个fragment
             findNavController().navigateUp()//代替下面写法
 //            Navigation.findNavController(view!!).navigateUp()
         }*/
    }
}


class LoginModel constructor(userName: String, password: String, private val context: Context) {
    val name = ObservableField<String>(userName)
    val pwd = ObservableField<String>(password)
    val loginEnabled = ObservableBoolean(false)

    /**
     * 监听密码输入
     */
    val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            pwd.set(s?.toString().orEmpty())
            checkLoginEnabled()
        }
    }

    /**
     * 用户名改变回调
     */
    fun onNameChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        name.set(s.toString())
        checkLoginEnabled()
    }

    fun checkLoginEnabled() {
        loginEnabled.set(name.get().isNotNullAndNotEmpty() && pwd.get().isNotNullAndNotEmpty())
    }

    /**
     * 模拟网络请求登录
     */
    fun login() {
        if (name.get() == LOGIN_USER_NAME && pwd.get() == LOGIN_PASSWORD) {
            //登录成功，跳转主页
            (context as? Activity)?.let { ctx ->
                NikeMainActivity.launch(ctx)
            }
        }
    }

    companion object {
        const val LOGIN_USER_NAME = "hzj"
        const val LOGIN_PASSWORD = "123456"
    }
}