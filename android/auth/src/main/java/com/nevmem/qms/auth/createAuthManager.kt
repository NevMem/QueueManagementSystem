package com.nevmem.qms.auth

import com.nevmem.qms.auth.internal.debug.DebugAuthManager
import com.nevmem.qms.auth.internal.impl.AuthManagerImpl

fun createDebugAuthManager(): AuthManager = DebugAuthManager()

fun createAuthManager(): AuthManager = AuthManagerImpl()
