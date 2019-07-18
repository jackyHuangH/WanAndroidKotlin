package com.jacky.wanandroidkotlin.ui.tabnavigation

import androidx.lifecycle.MutableLiveData
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.entity.NavigationEntity
import com.jacky.wanandroidkotlin.model.repositry.NavRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author:Hzj
 * @date  :2019-07-18
 * desc  ：
 * record：
 */
class NavViewModel : BaseViewModel() {
    private val mRepository by lazy { NavRepository() }
    val mNavList: MutableLiveData<List<NavigationEntity>> = MutableLiveData()

    fun getNavigation() {
        launch {
            val result = withContext(Dispatchers.IO) { mRepository.getNavigationList() }
            executeResponse(result, { mNavList.value = result.data }, { mErrorMsg.value = result.errorMsg })
        }
    }

}