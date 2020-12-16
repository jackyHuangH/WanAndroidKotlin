package com.jacky.wanandroidkotlin.jetpack.binding

import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter

/**
 * @author:Hzj
 * @date  :2020/8/28
 * desc  ：dataBinding 提供了 BindingAdapter 这个注解用于支持自定义属性，或者是修改原有属性。
 * 注解值可以是已有的 xml 属性，例如 android:src、android:text等，也可以自定义属性然后在 xml 中使用
 * record：
 */

/**
 * 为EditText绑定输入监听
 */
@BindingAdapter(value = ["addTextWatcher"], requireAll = true)
fun addTextWatcher(editText: EditText, textWatcher: TextWatcher) {
    editText.addTextChangedListener(textWatcher)
}

/**
 * imageView设置本地图片资源
 */
@BindingAdapter(value = ["imgSrc"])
fun imgSrcRes(imageView: ImageView, resId: Int) {
    imageView.setImageResource(resId)
}

/**
 * imageView设置是否选中
 */
@BindingAdapter(value = ["imgSelected"])
fun imgSelected(imageView: ImageView, selected: Boolean) {
    imageView.isSelected = selected
}