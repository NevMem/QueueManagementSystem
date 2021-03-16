package com.nevmem.qms.network.internal.actual.services

import retrofit2.Call
import retrofit2.http.GET

interface FeaturesService {
    @GET("/config/56cf679c-beb3-4f64-ac63-b8d3697b9cc2")
    fun loadFeatures(): Call<Map<String, String>>
}
