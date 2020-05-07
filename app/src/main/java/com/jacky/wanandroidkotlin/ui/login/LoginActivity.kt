package com.jacky.wanandroidkotlin.ui.login

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.ui.main.MainActivity
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.zenchn.support.router.Router
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar_layout.*

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


    override fun getLayoutRes(): Int = R.layout.activity_login

    override fun initWidget() {
        toolbar.title = getString(R.string.login_bt_login)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        bt_login.setOnClickListener { login() }
        bt_register.setOnClickListener { register() }
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

    override fun startObserve() {
        mViewModel.apply {
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