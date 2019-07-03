package com.jacky.wanandroidkotlin.ui.login

import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.entity.UserEntity
import com.jacky.wanandroidkotlin.model.repositry.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author:Hzj
 * @date  :2019/7/3/003
 * desc  ：
 * record：
 */
class LoginViewModel : BaseViewModel() {
    private val mRepository by lazy { UserRepository() }
    val mLoginUserEntity: MutableLiveData<UserEntity> = MutableLiveData()
    val mRegisterUserEntity: MutableLiveData<UserEntity> = MutableLiveData()

    fun login(username: String, password: String) {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.login(username, password) }
            executeResponse(
                response,
                { mLoginUserEntity.value = response.data },
                { mErrorMsg.value = response.errorMsg })
        }
    }

    fun register(username: String, password: String) {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.register(username, password, password) }
            executeResponse(response,
                { mRegisterUserEntity.value = response.data },
                { mErrorMsg.value = response.errorMsg })
        }
    }
}