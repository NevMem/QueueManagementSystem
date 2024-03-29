package com.nevmem.qms.auth

import com.nevmem.qms.auth.internal.impl.AuthManagerImpl
import com.nevmem.qms.keyvalue.KeyValueStorage
import com.nevmem.qms.logger.Logger
import com.nevmem.qms.network.NetworkManager

fun createAuthManager(
    storage: KeyValueStorage,
    networkManager: NetworkManager,
    logger: Logger
): AuthManager = AuthManagerImpl(storage, networkManager, logger)
