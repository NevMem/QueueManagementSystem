package com.nevmem.qms.permissions

enum class PermissionStatus {
    GRANTED,
    DENIED
}

data class PartitionedPermissionsResponse(val permission: Permission, val status: PermissionStatus)

data class PermissionsResponse(val partition: List<PartitionedPermissionsResponse>) {
    val isAllGranted: Boolean
        get() = partition.all { it.status == PermissionStatus.GRANTED }
    val denied: List<Permission>
        get() = partition.filter { it.status == PermissionStatus.DENIED }.map { it.permission }
}
