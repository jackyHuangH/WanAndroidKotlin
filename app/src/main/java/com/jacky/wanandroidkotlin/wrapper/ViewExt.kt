package com.jacky.wanandroidkotlin.wrapper

import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.IdRes
import com.zenchn.support.base.IActivity

/**
 * @author:Hzj
 * @date  :2020/12/21
 * desc  ：View相关扩展函数
 * record：
 */

//根据ID获取view
fun <V : View> IActivity.getView(@IdRes viewId: Int): V = findViewWithId<V>(viewId)

//获取textiew，editText的文字
fun IActivity.getTextString(@IdRes viewId: Int): String =
    getView<TextView>(viewId).text?.toString() ?: ""

//避免直接引用相同名称id造成错乱难以排查
fun <V : View> IActivity.viewExt(@IdRes viewId: Int, extra: V.() -> Unit) {
    val view = getView<V>(viewId)
    view.run(extra)
}

//获取editText 内容变化后的内容
fun IActivity.getEditChangeText(@IdRes viewId: Int, afterTextChange: (String) -> Unit) {
    val view: EditText = getView<EditText>(viewId)
    view.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChange.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}

//对View的子view操作
fun <V : View> View.childViewExt(
    @IdRes childId: Int,
    extra: V.() -> Unit
) = findViewById<V>(childId)?.run {
    extra.invoke(this)
}

//设置是否可见
fun IActivity.viewVisibleExt(@IdRes viewId: Int, visible: Boolean) {
    viewExt<View>(viewId) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }
}

//设置是否可见占位
fun IActivity.viewInvisibleExt(@IdRes viewId: Int, visible: Boolean) {
    viewExt<View>(viewId) {
        visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }
}

//点击事件封装
fun IActivity.viewClickListener(@IdRes viewId: Int, click: (View) -> Unit) {
    viewExt<View>(viewId) {
        setOnAntiShakeClickListener { click.invoke(this) }
    }
}

/**
 * 按钮点击防抖
 */
fun View?.setOnAntiShakeClickListener(intervalMillis: Long = 1000, listener: (View) -> Unit) {
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


//判断2个参数都不为null
fun <M, N, R> bothSafeRun(m: M?, n: N?, block: (M, N) -> R?): R? {
    return if (m != null && n != null) {
        block.invoke(m, n)
    } else {
        null
    }
}