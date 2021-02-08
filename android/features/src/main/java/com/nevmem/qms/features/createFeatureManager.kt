package com.nevmem.qms.features

import com.nevmem.qms.features.internal.FeatureManagerImpl
import com.nevmem.qms.keyvalue.KeyValueStorage
import com.nevmem.qms.logger.Logger
import com.nevmem.qms.network.NetworkManager

fun createFeatureManager(
    networkManager: NetworkManager,
    storage: KeyValueStorage,
    logger: Logger
): FeatureManager = FeatureManagerImpl(networkManager, storage, logger)
