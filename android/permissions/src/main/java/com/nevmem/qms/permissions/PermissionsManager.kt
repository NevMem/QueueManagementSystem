package com.nevmem.qms.permissions


interface PermissionsManager {
    val hasRequests: Boolean

    fun popRequest(): PermissionsRequest

    fun requestPermissions(permissions: List<Permission>, callback: (PermissionsResponse) -> Unit = {})
    fun hasPermission(permission: Permission): Boolean

    fun registerDelegate(delegate: PermissionsDelegate)
    fun removeDelegate(delegate: PermissionsDelegate)
}
