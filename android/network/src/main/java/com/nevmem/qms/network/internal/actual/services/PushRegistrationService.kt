package com.nevmem.qms.network.internal.actual.services

import com.nevmem.qms.data.NewPushTokenRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PushRegistrationService {
    @POST("/api/push/register")
    fun registerNewPushToken(@Body request: NewPushTokenRequest): Call<Any>
}
