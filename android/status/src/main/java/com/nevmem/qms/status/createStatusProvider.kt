package com.nevmem.qms.status

import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.status.internal.StatusProviderImpl


fun createDebugStatusProvider(networkManager: NetworkManager): StatusProvider = StatusProviderImpl(networkManager)
