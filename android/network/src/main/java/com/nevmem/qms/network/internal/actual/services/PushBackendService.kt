package com.nevmem.qms.network.internal.actual.services

import com.nevmem.qms.data.NewPushTokenRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PushBackendService {
    @POST("api/push/register")
    fun registerNewPushToken(@Header("session") session: String, @Body request: NewPushTokenRequest): Call<Unit>
}
