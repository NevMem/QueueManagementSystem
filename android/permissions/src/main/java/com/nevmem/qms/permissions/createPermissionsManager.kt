package com.nevmem.qms.permissions

import com.nevmem.qms.permissions.internal.PermissionsManagerImpl

fun createPermissionsManager(): PermissionsManager = PermissionsManagerImpl()
