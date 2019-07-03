package com.jacky.wanandroidkotlin.ui.main

import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.repositry.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author:Hzj
 * @date  :2019/7/3/003
 * desc  ：首页
 * record：
 */
class MainViewModel : BaseViewModel() {
    val mLogoutInfo: MutableLiveData<String> = MutableLiveData()
    private val mRepository by lazy { UserRepository() }

    fun logout() {
        launch {
            val response = withContext(Dispatchers.IO) { mRepository.logout() }
            executeResponse(response, { mLogoutInfo.value = "退出登录成功！" }, { mErrorMsg.value = response.errorMsg })
        }
    }
}