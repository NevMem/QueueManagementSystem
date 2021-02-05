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
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import kotlin.coroutines.suspendCoroutine

class NetworkManagerImpl : NetworkManager {

    data class UserIdentity(val email: String?, val password: String?)
    data class RegisterRequest(val name: String?, val surname: String?, val identity: UserIdentity?)
    data class User(val id: String?, val name: String?, val surname: String?, val email: String?)

    interface BackendService {
        @POST("/client/login")
        fun login(@Body body: UserIdentity?): Call<ClientApiProto.AuthResponse>

        @POST("/client/register")
        fun register(@Body body: RegisterRequest?): Call<Any>

        @POST("/client/get_user")
        fun getUser(@Header("session") session: String): Call<ClientApiProto.User>
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
        service.login(credentials.toDataClass()).enqueue(object : Callback<ClientApiProto.AuthResponse> {
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
        service.register(credentials.toDataClass()).enqueue(object : Callback<Any> {
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

    override suspend fun getUser(token: String): ClientApiProto.User = suspendCoroutine { continuation ->
        service.getUser(token).enqueue(object : Callback<ClientApiProto.User> {
            override fun onFailure(call: Call<ClientApiProto.User>, t: Throwable) {
                continuation.resumeWith(Result.failure(t))
            }

            override fun onResponse(
                call: Call<ClientApiProto.User>,
                response: Response<ClientApiProto.User>
            ) {
                if (response.code() == 200 && response.body() != null) {
                    continuation.resumeWith(Result.success(response.body()!!))
                } else {
                    continuation.resumeWith(Result.failure(IllegalStateException("Response code isn't 200 or body not present")))
                }
            }
        })
    }

    private fun ClientApiProto.RegisterRequest?.toDataClass(): RegisterRequest? {
        if (this == null) {
            return null
        }
        return RegisterRequest(name, surname, identity.toDataClass())
    }

    private fun ClientApiProto.UserIdentity?.toDataClass(): UserIdentity? {
        if (this == null) {
            return null
        }
        return UserIdentity(email, password)
    }
}
