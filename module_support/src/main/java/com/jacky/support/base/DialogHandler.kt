/*
 * Copyright (C) 2017 Haoge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jacky.support.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.ContextThemeWrapper

/**
 * 用于安全的进行Dialog显示隐藏的工具类
 *
 * @author haoge
 */
object DialogHandler {
    fun safeShowDialog(dialog: Dialog?) {
        if (dialog == null || dialog.isShowing) {
            return
        }
        val bindAct = getActivity(dialog)
        if (!actIsValid(bindAct)) {
            Log.d(
                "Dialog shown failed:%s",
                "The Dialog bind's Activity was recycled or finished!"
            )
            return
        }
        dialog.show()
    }

    private fun getActivity(dialog: Dialog): Activity? {
        var bindAct: Activity? = null
        var context: Context? = dialog.context
        do {
            if (context is Activity) {
                bindAct = context
                break
            } else if (context is ContextThemeWrapper) {
                context = context.baseContext
            } else {
                break
            }
        } while (true)
        return bindAct
    }

    @JvmStatic
    fun safeDismissDialog(dialog: Dialog?) {
        var dialog = dialog
        if (dialog == null || !dialog.isShowing) {
            return
        }
        val bindAct = getActivity(dialog)
        if (bindAct != null && !bindAct.isFinishing) {
            dialog.dismiss()
            dialog = null
        }
    }

    fun actIsValid(activity: Activity?): Boolean {
        return activity != null && !activity.isFinishing
    }
}