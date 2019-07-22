package com.jacky.wanandroidkotlin.wrapper

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.jacky.wanandroidkotlin.R

/**
 * @author:Hzj
 * @date  :2019/7/3/003
 * desc  ：Materia Dialog提供类
 * record：
 */
object DialogProvider {

    /**
     * 提供简单的提示框
     */
    fun showSimpleDialog(context: Context, content: String, onConfirm: () -> Unit) {
        MaterialDialog(context).show {
            title(R.string.material_dialog_common_title)
            message(text = content)
            positiveButton(R.string.material_dialog_common_positive) {
                onConfirm()
                it.dismiss()
            }
            negativeButton(R.string.material_dialog_common_negative) {
                it.dismiss()
            }
        }
    }
}