package com.nevmem.qms.rating.internal

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.common.utils.retry
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.rating.Rating
import com.nevmem.qms.rating.RatingsManager

internal class RatingsManagerImpl(
    private val authManager: AuthManager,
    private val networkManager: NetworkManager
) : RatingsManager {

    override suspend fun loadRating(ratingId: String): Rating
        = Rating(retry({ networkManager.loadRating(ratingId, authManager.token) }), 5.0f)
}
