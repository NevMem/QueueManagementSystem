package com.nevmem.qms.network.internal.actual.services

import com.nevmem.qms.data.NewPushTokenRequest
import com.nevmem.qms.data.feedback.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface JavaBackendService {
    @POST("api/push/register")
    fun registerNewPushToken(@Body request: NewPushTokenRequest): Call<Unit>

    @POST("api/feedback/publish")
    fun publishFeedback(@Body request: PublishFeedbackRequest, @Header("session") session: String): Call<Unit>

    @POST("api/feedback/load")
    fun loadFeedback(@Header("session") session: String, @Body body: LoadFeedbacksRequest): Call<List<Feedback>>

    @POST("api/rating")
    fun loadRating(@Header("session") session: String, @Body body: LoadRatingRequest): Call<LoadRatingResponse>
}
