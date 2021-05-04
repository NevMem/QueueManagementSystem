package com.nevmem.qms.history

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.history.internal.HistoryManagerImpl
import com.nevmem.qms.network.NetworkManager

fun createHistoryManager(
    authManager: AuthManager, networkManager: NetworkManager
): HistoryManager = HistoryManagerImpl(authManager, networkManager)
