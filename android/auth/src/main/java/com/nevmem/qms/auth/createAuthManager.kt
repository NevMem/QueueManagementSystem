package com.nevmem.qms.auth

import com.nevmem.qms.auth.internal.debug.DebugAuthManager
import com.nevmem.qms.auth.internal.impl.AuthManagerImpl
import com.nevmem.qms.keyvalue.KeyValueStorage

fun createDebugAuthManager(storage: KeyValueStorage): AuthManager = DebugAuthManager(storage)

fun createAuthManager(): AuthManager = AuthManagerImpl()
