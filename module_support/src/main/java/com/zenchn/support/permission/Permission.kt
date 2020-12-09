package com.zenchn.support.permission

import com.zenchn.support.base.IActivity
import org.jetbrains.annotations.NotNull

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


