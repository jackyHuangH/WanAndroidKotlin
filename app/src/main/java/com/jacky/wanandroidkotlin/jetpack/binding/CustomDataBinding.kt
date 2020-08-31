package com.jacky.wanandroidkotlin.jetpack.binding

import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.BindingAdapter

/**
 * @author:Hzj
 * @date  :2020/8/28
 * desc  ：自定义binder函数绑定
 * record：
 */

/**
 * 为EditText绑定输入监听
 */
@BindingAdapter(value = ["addTextWatcher"], requireAll = true)
fun addTextWatcher(editText: EditText, textWatcher: TextWatcher) {
    editText.addTextChangedListener(textWatcher)
}