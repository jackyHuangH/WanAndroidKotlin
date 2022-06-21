package com.jacky.wanandroidkotlin.test

/**
 * @author:Hzj
 * @date  :2022/6/10
 * desc  ：Room测试
 * record：
 */
import android.app.Application
import android.graphics.BitmapFactory
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.databinding.ActivityRoomTestBinding
import com.jacky.wanandroidkotlin.jetpack.room.TestDataBase
import com.jacky.wanandroidkotlin.jetpack.room.UserEntity
import com.jacky.wanandroidkotlin.wrapper.viewClickListener
import com.jacky.wanandroidkotlin.wrapper.viewExt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RoomTestActivity : BaseVMActivity<ActivityRoomTestBinding,RoomTestViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_room_test

    override fun initWidget() {
        viewClickListener(R.id.bt_add) {
            //todo 新增user
            val user = UserEntity(null, "张三", 43, "杭州市西湖区1号")
            mViewModel.addUser(user)
        }
        viewClickListener(R.id.bt_query) {
            //todo 查询所有
            mViewModel.getAllUser()
        }
        //implicit explicit
    }

    override val startObserve: RoomTestViewModel.() -> Unit = {
        listData.observe(this@RoomTestActivity, Observer {
            viewExt<TextView>(R.id.tv_log){
                text=it.toString()
            }
        })
    }
}

class RoomTestViewModel(application: Application) : BaseViewModel(application) {

    val listData = MutableLiveData<List<UserEntity>>()

    private val userDao by lazy { TestDataBase.getInstance().userDao() }

    fun addUser(user: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.insert(user)
        }
    }

    fun getAllUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = userDao.queryAll()
            listData.postValue(list)
        }
    }

}