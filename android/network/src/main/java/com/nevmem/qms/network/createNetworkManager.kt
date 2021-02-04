package com.nevmem.qms.network

import com.nevmem.qms.network.internal.actual.NetworkManagerImpl
import com.nevmem.qms.network.internal.debug.DebugNetworkManager

fun createNetworkManager(): NetworkManager = NetworkManagerImpl()

fun createDebugNetworkManager(): NetworkManager = DebugNetworkManager()
