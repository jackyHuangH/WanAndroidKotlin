package com.jacky.wanandroidkotlin

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jacky.wanandroidkotlin.livedatatest.NameViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        //使用viewmodel展示数据
        val nameViewModel = ViewModelProviders.of(this).get(NameViewModel::class.java)
        nameViewModel.currentName().observe(this, Observer {
            print(it)
        })

        nameViewModel.getNameList().observe(this, Observer {
            if (it != null) {
                val sb = StringBuilder()
                for (item in it) {
                    sb.append(item)
                }
                tv.text = sb.toString()
            }
        })
    }
}
