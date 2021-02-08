package com.nevmem.qms.network

import com.nevmem.qms.logger.Logger
import com.nevmem.qms.network.internal.actual.NetworkManagerImpl
import com.nevmem.qms.network.internal.debug.DebugNetworkManager

fun createNetworkManager(logger: Logger): NetworkManager = NetworkManagerImpl(logger)

fun createDebugNetworkManager(): NetworkManager = DebugNetworkManager()
