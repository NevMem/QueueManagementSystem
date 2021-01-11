package com.nevmem.qms.features

import com.nevmem.qms.features.internal.FeatureManagerImpl
import com.nevmem.qms.network.NetworkManager

fun createFeatureManager(networkManager: NetworkManager): FeatureManager
    = FeatureManagerImpl(networkManager)
