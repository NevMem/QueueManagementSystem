package com.nevmem.qms.auth

import com.nevmem.qms.auth.internal.debug.DebugAuthManager
import com.nevmem.qms.auth.internal.impl.AuthManagerImpl
import com.nevmem.qms.keyvalue.KeyValueStorage
import com.nevmem.qms.network.NetworkManager

fun createDebugAuthManager(storage: KeyValueStorage): AuthManager = DebugAuthManager(storage)

fun createAuthManager(
    storage: KeyValueStorage,
    networkManager: NetworkManager
): AuthManager = AuthManagerImpl(storage, networkManager)
