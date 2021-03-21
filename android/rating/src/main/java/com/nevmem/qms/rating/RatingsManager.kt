package com.nevmem.qms.rating

interface RatingsManager {
    suspend fun loadRating(ratingId: String): Rating
}
