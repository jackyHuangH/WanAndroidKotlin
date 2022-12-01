package com.jacky.wanandroidkotlin.test

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jacky.support.router.Router
import com.jacky.wanandroidkotlin.R
import java.util.*

/**
 * 拼图游戏
 */
class PuzzleActivity : AppCompatActivity() {
    private var mTvTime: TextView? = null
    private var mBtnRestart: Button? = null
    private val imageX = 3
    private val imageY = 3
    private var blank = imageX * imageY - 1
    private val mIbtArray = arrayOfNulls<ImageButton>(imageX * imageY)
    private val image = intArrayOf(
        R.drawable.img_xiaoxiong_00x00,
        R.drawable.img_xiaoxiong_00x01,
        R.drawable.img_xiaoxiong_00x02,
        R.drawable.img_xiaoxiong_01x00,
        R.drawable.img_xiaoxiong_01x01,
        R.drawable.img_xiaoxiong_01x02,
        R.drawable.img_xiaoxiong_02x00,
        R.drawable.img_xiaoxiong_02x01,
        R.drawable.img_xiaoxiong_02x02
    )
    private val imageIndex = IntArray(image.size)
    private var time = 0
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                time++
                mTvTime?.text = String.format(Locale.CHINA, "时间:%d秒", time)
                sendEmptyMessageDelayed(1, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_game)
        initView()
        upsetRandom()
        mHandler.sendEmptyMessageDelayed(1, 1000)
    }

    /**
     * 随机打乱图片
     */
    private fun upsetRandom() {
        for (i in imageIndex.indices) {
            imageIndex[i] = i
        }
        var rand1: Int
        var rand2: Int
        for (i in 0..19) {
            rand1 = (Math.random() * (imageIndex.size - 1)).toInt()
            do {
                rand2 = (Math.random() * (imageIndex.size - 1)).toInt()
            } while (rand1 == rand2)
            swap(rand1, rand2)
        }
        for (i in 0 until mIbtArray.size - 1) {
            mIbtArray[i]?.setImageResource(image[imageIndex[i]])
            mIbtArray[i]?.isClickable = true
        }
        //最后一张默认不可见
        mIbtArray[mIbtArray.size - 1]?.visibility = View.INVISIBLE
    }

    private fun swap(rand1: Int, rand2: Int) {
        val t: Int = imageIndex[rand1]
        imageIndex[rand1] = imageIndex[rand2]
        imageIndex[rand2] = t
    }

    private fun initView() {
        mTvTime = findViewById(R.id.tv_time)
        mBtnRestart = findViewById(R.id.btn_restart)
        mIbtArray[0] = findViewById(R.id.IB_00X00)
        mIbtArray[1] = findViewById(R.id.IB_00X01)
        mIbtArray[2] = findViewById(R.id.IB_00X02)
        mIbtArray[3] = findViewById(R.id.IB_01X00)
        mIbtArray[4] = findViewById(R.id.IB_01X01)
        mIbtArray[5] = findViewById(R.id.IB_01X02)
        mIbtArray[6] = findViewById(R.id.IB_02X00)
        mIbtArray[7] = findViewById(R.id.IB_02X01)
        mIbtArray[8] = findViewById(R.id.IB_02X02)
    }

    private fun move(site: Int) {
        val sitex = site / imageX
        val sitey = site % imageY
        val blankx = blank / imageX
        val blanky = blank % imageY
        val x = Math.abs(sitex - blankx)
        val y = Math.abs(sitey - blanky)
        if (x == 0 && y == 1 || x == 1 && y == 0) {
            mIbtArray[site]?.visibility = View.INVISIBLE
            mIbtArray[blank]?.visibility = View.VISIBLE
            mIbtArray[blank]?.setImageResource(image[imageIndex[site]])
            swap(site, blank)
            blank = site
        }
        gameOver()
    }

    private fun gameOver() {
        var loop = true
        for (i in imageIndex.indices) {
            if (imageIndex[i] != i) {
                loop = false
                break
            }
        }
        if (loop) {
            mHandler.removeMessages(1)
            for (i in 0 until mIbtArray.size - 1) {
                mIbtArray[i]?.isClickable = false
            }
            mIbtArray[mIbtArray.size - 1]?.visibility = View.VISIBLE
            val builder = AlertDialog.Builder(this)
            builder.setMessage("恭喜，拼图成功")
            builder.create()
            builder.show()
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.IB_00X00 -> {
                move(0)
            }
            R.id.IB_00X01 -> {
                move(1)
            }
            R.id.IB_00X02 -> {
                move(2)
            }
            R.id.IB_01X00 -> {
                move(3)
            }
            R.id.IB_01X01 -> {
                move(4)
            }
            R.id.IB_01X02 -> {
                move(5)
            }
            R.id.IB_02X00 -> {
                move(6)
            }
            R.id.IB_02X01 -> {
                move(7)
            }
            R.id.IB_02X02 -> {
                move(8)
            }
        }
    }

    fun restart(view: View?) {
        upsetRandom()
        mHandler.removeMessages(1)
        time = 0
        mHandler.sendEmptyMessage(1)
    }

    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(PuzzleActivity::class.java)
                .launch()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
}