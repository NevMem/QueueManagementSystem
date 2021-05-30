package com.nevmem.qms.network.internal.actual.services

import com.nevmem.qms.data.feedback.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FeedbackBackendService {
    @POST("api/feedback/publish")
    fun publishFeedback(@Header("session") session: String, @Body request: PublishFeedbackRequest): Call<Unit>

    @POST("api/feedback/load")
    fun loadFeedback(@Header("session") session: String, @Body body: LoadFeedbacksRequest): Call<List<Feedback>>

    @POST("api/rating")
    fun loadRating(@Header("session") session: String, @Body body: LoadRatingRequest): Call<LoadRatingResponse>
}
