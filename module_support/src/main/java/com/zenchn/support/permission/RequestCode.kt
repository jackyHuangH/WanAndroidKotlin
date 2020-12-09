package com.zenchn.support.permission

import androidx.annotation.IntDef

interface RequestCode {

    @kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
    @IntDef(DEFAULT,
            OS_SETTING, OS_CALL_PHONE, OS_CAMERA, OS_GALLERY,
            EDIT_HOME_MENU, COORDINATE_PICK_UP,EDIT_SWITCH_INFO,
            READ_MESSAGE, EVENT_PROCESS
    )
    annotation class Scope

    companion object {

        const val DEFAULT = 1

        const val OS_SETTING = 10
        const val OS_CALL_PHONE = 11
        const val OS_CAMERA = 12
        const val OS_GALLERY = 13
        const val EDIT_HOME_MENU = 15
        const val EDIT_SWITCH_INFO = 16

        const val COORDINATE_PICK_UP = 31
        const val READ_MESSAGE = 41
        const val EVENT_PROCESS = 51

    }

}