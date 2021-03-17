package com.nevmem.qms.network

import com.nevmem.qms.logger.Logger
import com.nevmem.qms.network.internal.actual.NetworkManagerImpl

fun createNetworkManager(logger: Logger): NetworkManager = NetworkManagerImpl(logger)
