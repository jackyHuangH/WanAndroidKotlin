package com.jacky.wanandroidkotlin.jetpack.binding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.databinding.TwoDataBinding
import kotlinx.android.synthetic.main.activity_three.*

/**
 * @author:Hzj
 * @date  :2020/11/27
 * desc  :DataBinding learning
 * record：
 */
class TwoActivity : AppCompatActivity() {

    private lateinit var mActivityBinding: TwoDataBinding
    private lateinit var mPeopleBean: PeopleBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Two", "onCreate")
        //1.设置dataBinding绑定关系
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_two)
        //2.实例化数据
        mPeopleBean = PeopleBean(ObservableField("张三"), ObservableField(25))
        //3.绑定数据
        mActivityBinding.data2 = mPeopleBean

        mActivityBinding.btChangeName.text = "改变name"
        mActivityBinding.btAddAge.text = "add age"
        mActivityBinding.clickListener = MyClickListener(mPeopleBean)
        initWidget()
    }

    private fun initWidget() {

    }

    override fun onPause() {
        super.onPause()
        Log.d("Two", "onPause")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("Two", "onNewIntent")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Two", "onResume")
    }
}

class MyClickListener(private val peopleBean: PeopleBean) {
    //4.更新ObservableField数据
    fun changeName() {
        peopleBean.name.set("我的新名字叫唯独你不懂")
    }

    fun addAge() {
        //更新ObservableField数据
        val newAge = peopleBean.age.get()?.plus(1)
        peopleBean.name.set("我改变所有UI了$newAge")
        peopleBean.age.set(newAge)
    }
}

class ThreeActivity : BaseActivity() {
    companion object {
        const val Tag = "Three"
    }

    override fun getLayoutId(): Int = R.layout.activity_three

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(Tag, "onCreate")
    }

    override fun initWidget() {
        tv_jump_self.setOnClickListener {
            startActivity(Intent(this, ThreeActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(Tag, "onPause")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(Tag, "onNewIntent")
    }

    override fun onResume() {
        super.onResume()
        Log.d(Tag, "onResume")
    }

}