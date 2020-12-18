package com.jacky.wanandroidkotlin.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jacky.wanandroidkotlin.wrapper.orNotNullNotEmpty
import java.util.*

/**
 * Created by hzj on 2018/11/14.
 * 扩展函数
 */

fun Activity.showToast(content: CharSequence): Toast {
    val toast = Toast.makeText(this, content, Toast.LENGTH_SHORT)
    toast.show()
    return toast
}

fun Fragment.showToast(content: CharSequence): Toast {
    val toast = Toast.makeText(this.activity?.applicationContext, content, Toast.LENGTH_SHORT)
    toast.show()
    return toast
}

fun View.dip2px(dipValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

fun View.px2dip(pxValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

fun durationFormat(duration: Long?): String {
    val minute = duration!! / 60
    val second = duration % 60
    return if (minute <= 9) {
        if (second <= 9) {
            "0$minute' 0$second''"
        } else {
            "0$minute' $second''"
        }
    } else {
        if (second <= 9) {
            "$minute' 0$second''"
        } else {
            "$minute' $second''"
        }
    }
}

/**
 * 数据流量格式化
 */
fun Context.dataFormat(total: Long): String {
    var result: String
    var speedReal: Int = (total / (1024)).toInt()
    result = if (speedReal < 512) {
        speedReal.toString() + " KB"
    } else {
        val mSpeed = speedReal / 1024.0
        (Math.round(mSpeed * 100) / 100.0).toString() + " MB"
    }
    return result
}

/** 浏览器打开指定网页 */
fun Context.openBrowser(url: String) {
    Intent(Intent.ACTION_VIEW, Uri.parse(url)).run { startActivity(this) }
}

/**
 * 按钮点击防抖
 */
fun View?.setOnAntiShakeClickListener(intervalMillis: Long = 500, listener: (View) -> Unit) {
    /**
     * 最近一次点击的时间
     */
    var lastClickTime: Long = 0
    this?.setOnClickListener {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime >= intervalMillis) {
            listener.invoke(it)
            lastClickTime = currentTime
        }
    }
}

/**
 * 格式化音乐播放时间：将毫秒转换为分秒-00:00格式
 */
fun formatMusicTime(timeMs: Int): String {
    val totalSeconds = timeMs / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60

    return Formatter().format("%02d:%02d", minutes, seconds).toString().orNotNullNotEmpty("00:00")
}
