package com.nevmem.qms.permissions

import android.Manifest

enum class Permission(val androidPermission: String) {
    CAMERA(Manifest.permission.CAMERA)
}

data class PermissionsRequest(
    val permissions: List<Permission>,
    val callback: (PermissionsResponse) -> Unit)
