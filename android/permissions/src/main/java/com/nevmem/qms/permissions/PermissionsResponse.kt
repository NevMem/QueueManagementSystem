package com.nevmem.qms.permissions

enum class PermissionStatus {
    GRANTED,
    DENIED,
    DENIED_AND_CANNOT_RETRY
}

data class PartitionedPermissionsResponse(val permission: Permission, val status: PermissionStatus)

data class PermissionsResponse(val partition: List<PartitionedPermissionsResponse>) {
    val isAllGranted: Boolean
        get() = partition.all { it.status == PermissionStatus.GRANTED }
    val cannotRetry: Boolean
        get() = partition.any { it.status == PermissionStatus.DENIED_AND_CANNOT_RETRY }
    val denied: List<Permission>
        get() = partition.filter { it.status != PermissionStatus.GRANTED }.map { it.permission }
}
