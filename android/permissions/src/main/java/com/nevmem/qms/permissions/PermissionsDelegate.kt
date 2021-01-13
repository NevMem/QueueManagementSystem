package com.nevmem.qms.permissions

interface PermissionsDelegate {
    fun onHasNewPermissionsRequest()
    fun hasPermission(permission: Permission): Boolean
}
