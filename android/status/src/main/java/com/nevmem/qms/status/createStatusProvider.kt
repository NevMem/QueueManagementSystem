package com.nevmem.qms.status

import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.status.internal.DebugStatusProvider


fun createDebugStatusProvider(networkManager: NetworkManager): StatusProvider = DebugStatusProvider(networkManager)
