package com.jacky.wanandroidkotlin.jetpack.nike

import android.app.Activity
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.databinding.ActivityNikeMainBinding
import com.zenchn.support.router.Router

/**
 * @author:Hzj
 * @date  :2020/8/28
 * desc  ：
 * record：
 */
class NikeMainActivity : BaseActivity<ActivityNikeMainBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_nike_main

    override fun initWidget() {

    }


    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(NikeMainActivity::class.java)
                .launch()
        }
    }
}