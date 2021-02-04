package com.nevmem.qms.network.internal.actual

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.QueueProto
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.network.data.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import kotlin.coroutines.suspendCoroutine

class NetworkManagerImpl : NetworkManager {

    interface BackendService {
        @POST("/login")
        fun login(@Body body: ClientApiProto.UserIdentity): Call<ClientApiProto.AuthResponse>

        @POST("/register")
        fun register(@Body body: ClientApiProto.RegisterRequest): Call<Any>
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://qms.nikitonsky.tk")
        .build()

    private val service = retrofit.create(BackendService::class.java)

    override suspend fun fetchDataForInvite(invite: String): QueueProto.Queue {
        TODO("Not yet implemented")
    }

    override suspend fun loadFeatures(): Map<String, String> = mapOf()

    override suspend fun login(credentials: ClientApiProto.UserIdentity): String = suspendCoroutine { continuation ->
        service.login(credentials).enqueue(object : Callback<ClientApiProto.AuthResponse> {
            override fun onFailure(call: Call<ClientApiProto.AuthResponse>, t: Throwable) {
                continuation.resumeWith(Result.failure(t))
            }

            override fun onResponse(call: Call<ClientApiProto.AuthResponse>, response: Response<ClientApiProto.AuthResponse>) {
                val token = response.headers().get("session")
                if (token != null) {
                    continuation.resumeWith(Result.success(token))
                } else {
                    continuation.resumeWith(Result.failure(IllegalStateException("Token is not present in response headers")))
                }
            }
        })
    }

    override suspend fun register(credentials: ClientApiProto.RegisterRequest): RegisterResponse = suspendCoroutine { continuation ->
        service.register(credentials).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
                continuation.resumeWith(Result.failure(t))
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.code() == 200) {
                    continuation.resumeWith(Result.success(RegisterResponse.Success))
                } else {
                    continuation.resumeWith(Result.success(RegisterResponse.NotUniqueLogin))
                }
            }
        })
    }
}
