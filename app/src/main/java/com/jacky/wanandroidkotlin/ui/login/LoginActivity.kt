package com.jacky.wanandroidkotlin.ui.login

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.ui.main.MainActivity
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.viewClickListener
import com.jacky.wanandroidkotlin.wrapper.viewExt
import com.zenchn.support.router.Router

/**
 * @author:Hzj
 * @date  :2019/7/3/003
 * desc  ：登录页
 * record：
 */
class LoginActivity : BaseVMActivity<LoginViewModel>() {

    private var mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)
    private var mUserInfo by PreferenceUtil(PreferenceUtil.KEY_USER_INFO, "")
    private lateinit var mUserName: String
    private lateinit var mPassword: String


    override fun getLayoutId(): Int = R.layout.activity_login

    override fun initWidget() {
        viewExt<Toolbar>(R.id.toolbar) {
            title = getString(R.string.login_bt_login)
            setNavigationOnClickListener { onBackPressed() }
        }
        viewClickListener(R.id.bt_login) { login() }
        viewClickListener(R.id.bt_register) { register() }
    }

    private fun login() {
        //登录
        if (checkNotEmpty()) {
            showProgress(getString(R.string.login_doing_login))
            mViewModel.login(mUserName, mPassword)
        }
    }

    private fun register() {
        //注册
        if (checkNotEmpty()) {
            showProgress(getString(R.string.login_doing_register))
            mViewModel.register(mUserName, mPassword)
        }
    }

    private fun checkNotEmpty(): Boolean {
        val userNameLayout = getView<TextInputLayout>(R.id.userNameLayout)
        val pswLayout = getView<TextInputLayout>(R.id.pswLayout)
        mUserName = userNameLayout.editText?.text.toString()
        mPassword = pswLayout.editText?.text.toString()
        if (mUserName.isEmpty()) {
            userNameLayout.error = getString(R.string.login_error_name_empty)
            return false
        }
        userNameLayout.isErrorEnabled = false
        if (mPassword.isEmpty()) {
            pswLayout.error = getString(R.string.login_error_password_empty)
            return false
        }
        pswLayout.isErrorEnabled = false
        return true
    }

    override val startObserve: LoginViewModel.() -> Unit = {
        mLoginUserEntity.observe(this@LoginActivity, Observer {
            //登录成功
            hideProgress()
            showMessage(getString(R.string.login_login_success))
            mIsLogin = true
            //保存用户信息
            mUserInfo = Gson().toJson(it)
            //跳转首页
            MainActivity.launch(this@LoginActivity)
            finish()
        })
        mRegisterUserEntity.observe(this@LoginActivity, Observer {
            //注册成功，请登录
            hideProgress()
            showMessage(getString(R.string.login_register_success))
        })
        mErrorMsg.observe(this@LoginActivity, Observer {
            hideProgress()
            it?.let { showMessage(it) }
        })
    }

    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(LoginActivity::class.java)
                .launch()
        }
    }
}