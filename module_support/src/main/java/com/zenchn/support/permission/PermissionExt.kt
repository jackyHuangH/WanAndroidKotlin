package com.zenchn.support.permission

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Boot
import com.yanzhenjie.permission.runtime.option.RuntimeOption
import com.yanzhenjie.permission.source.Source
import com.zenchn.support.base.IActivity
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

interface IPermission

abstract class PermissionSource : Source()

const val REQUEST_CODE_OS_SETTING = 9999


fun IActivity.runOnPermissionsGranted(
    @NotNull vararg permissions: String,
    @NotNull granted: () -> Unit
) = checkSelfPermissions(*permissions) { _, hasPermissions ->
    if (hasPermissions) granted.invoke()
    else applySelfPermissionsStrict(*permissions) { granted.invoke() }
}

fun IActivity.applySelfPermissionsStrict(
    @NotNull vararg permissions: String,
    @NotNull onGranted: () -> Unit
) = applySelfPermissions(*permissions) { _, result ->
    if (result) onGranted.invoke()
    else navigateToPermissionSetting(RequestCode.OS_SETTING)
}

fun IActivity.checkSelfPermission(
    @NotNull vararg permissions: String,
    @NotNull onGranted: (granted: String) -> Unit,
    @NotNull onDenied: (denied: String) -> Unit
) = checkSelfPermissions(*permissions) { permission, hasPermission ->
    if (hasPermission) onGranted.invoke(permission)
    else onDenied.invoke(permission)
}


@Suppress("DEPRECATION")
private fun IPermission.runtime(): RuntimeOption = when (this) {
    is Activity -> AndPermission.with(this)
    is Context -> AndPermission.with(this)
    is Fragment -> AndPermission.with(this)
    is android.app.Fragment -> AndPermission.with(this)
    is PermissionSource -> Boot(this)
    else -> throw IllegalStateException("无法获取上下文！")
}.runtime()

fun IPermission.navigateToPermissionSetting(requestCode: Int = REQUEST_CODE_OS_SETTING) =
        runtime().setting().start(requestCode)

@SuppressLint("WrongConstant")
fun IPermission.applySelfPermissions(
        @NotNull vararg permissions: String,
        @Nullable result: ((List<String>, Boolean) -> Unit)? = null
) = runtime()
        .permission(*permissions)
        .apply {
            onGranted { granted -> result?.invoke(granted, true) }
            onDenied { denied -> result?.invoke(denied, false) }
        }
        .start()

@Suppress("DEPRECATION")
fun IPermission.checkSelfPermissions(@NotNull vararg permissions: String): Boolean = when (this) {
    is Activity -> this
    is Context -> this
    is Fragment -> context
    is android.app.Fragment -> activity
    is PermissionSource -> context
    else -> throw IllegalStateException("无法获取上下文！")
}.let { AndPermission.hasPermissions(it, permissions) }

@Suppress("DEPRECATION")
fun IPermission.checkSelfPermissions(
        @NotNull vararg permissions: String,
        @Nullable result: (String, Boolean) -> Unit
) = when (this) {
    is Activity -> this
    is Context -> this
    is Fragment -> context
    is android.app.Fragment -> activity
    is PermissionSource -> context
    else -> throw IllegalStateException("无法获取上下文！")
}.let { context ->
    permissions.forEach { permission ->
        AndPermission.hasPermissions(context, permissions).let { hasPermissions ->
            result.invoke(permission, hasPermissions)
        }
    }
}

fun IPermission.runOnPermissionsGranted(
    @NotNull vararg permissions: String,
    requestCode: Int = REQUEST_CODE_OS_SETTING,
    @NotNull granted: () -> Unit
) = checkSelfPermissions(*permissions) { _, hasPermissions ->
    if (hasPermissions) granted.invoke()
    else applySelfPermissions(*permissions) { _, result ->
        if (result) granted.invoke()
        else navigateToPermissionSetting(requestCode)
    }
}



