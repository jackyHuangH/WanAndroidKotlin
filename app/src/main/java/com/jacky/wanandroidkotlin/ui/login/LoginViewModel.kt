package com.jacky.wanandroidkotlin.ui.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.base.executeRequest
import com.jacky.wanandroidkotlin.model.entity.UserEntity
import com.jacky.wanandroidkotlin.model.repositry.UserRepository

/**
 * @author:Hzj
 * @date  :2019/7/3/003
 * desc  ：
 * record：
 */
class LoginViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository by lazy { UserRepository() }
    val mLoginUserEntity: MutableLiveData<UserEntity> = MutableLiveData()
    val mRegisterUserEntity: MutableLiveData<UserEntity> = MutableLiveData()

    fun login(username: String, password: String) {
        executeRequest(request = { mRepository.login(username, password) },
            onNext = { ok, data, _ ->
                if (ok) {
                    mLoginUserEntity.value = data
                }
            })
    }

    fun register(username: String, password: String) {
        executeRequest(request = { mRepository.register(username, password, password) },
            onNext = { ok, data, msg ->
                if (ok) {
                    mRegisterUserEntity.value = data
                }
            })
    }
}