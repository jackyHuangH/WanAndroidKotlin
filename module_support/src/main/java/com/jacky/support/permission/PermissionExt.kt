package com.jacky.support.permission

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

interface IPermission


/**
 * 跳转应用设置页面
 */
@Suppress("DEPRECATION")
fun IPermission.openPermissionSetting(
    @NotNull vararg permissions: String
) = when (this) {
    is Activity -> this
    is Context -> this
    is Fragment -> context
    is android.app.Fragment -> activity
    else -> throw IllegalStateException("无法获取上下文！")
}.let { ctx ->
    // 如果是被永久拒绝就跳转到应用权限系统设置页面
    XXPermissions.startPermissionActivity(ctx, permissions)
}


/**
 * 检查权限是否已授权
 */
@Suppress("DEPRECATION")
fun IPermission.checkPermissionsGranted(@NotNull vararg permissions: String): Boolean =
    when (this) {
        is Activity -> this
        is Context -> this
        is Fragment -> context
        is android.app.Fragment -> activity
        else -> throw IllegalStateException("无法获取上下文！")
    }.let { XXPermissions.isGranted(it, permissions) }

/**
 * 检查权限是否已授权
 */
@Suppress("DEPRECATION")
fun IPermission.checkPermissionsGranted(
    @NotNull vararg permissions: String,
    @Nullable result: (Boolean) -> Unit
) = when (this) {
    is Activity -> this
    is Context -> this
    is Fragment -> context
    is android.app.Fragment -> activity
    else -> throw IllegalStateException("无法获取上下文！")
}.let { context ->
    XXPermissions.isGranted(context, permissions).let { granted ->
        result.invoke(granted)
    }
}

/**
 * 检查权限是否已永久拒绝
 */
@Suppress("DEPRECATION")
fun IPermission.checkPermanentDenied(
    @NotNull vararg permissions: String,
    @Nullable result: (Boolean) -> Unit
) = when (this) {
    is Activity -> this
    is android.app.Fragment -> activity
    else -> throw IllegalStateException("无法获取上下文！")
}.let { context ->
    XXPermissions.isPermanentDenied(context, permissions).let { granted ->
        result.invoke(granted)
    }
}

/**
 * 请求权限
 */
fun IPermission.requestSelfPermissions(
    @NotNull vararg permissions: String,
    @NotNull callback: (granted: Boolean, never: Boolean) -> Unit
) = when (this) {
    is Activity -> this
    is Context -> this
    is Fragment -> context
    else -> throw IllegalStateException("无法获取上下文！")
}.let { context ->
    XXPermissions.with(context) // 申请单个权限
        .permission(*permissions)
        .request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (all) {
                    callback.invoke(true, false)
                }
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                callback.invoke(false, never)
            }
        })
}




