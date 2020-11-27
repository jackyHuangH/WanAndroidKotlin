package com.jacky.wanandroidkotlin.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import kotlinx.android.synthetic.main.activity_three.*
import kotlinx.android.synthetic.main.activity_two.*

/**
 * @author:Hzj
 * @date  :2020/11/27
 * desc  ：
 * record：
 */
class TwoActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_two

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Two", "onCreate")
    }

    override fun initWidget() {
        tv.setOnClickListener {
            startActivity(Intent(this, ThreeActivity::class.java))
        }
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