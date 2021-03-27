package com.nevmem.qms.rating

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.rating.internal.RatingsManagerImpl

fun createRatingsManager(
    authManager: AuthManager,
    networkManager: NetworkManager
): RatingsManager = RatingsManagerImpl(authManager, networkManager)
