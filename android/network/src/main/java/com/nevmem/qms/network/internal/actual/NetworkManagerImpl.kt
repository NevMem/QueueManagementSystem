package com.nevmem.qms.network.internal.actual

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.QueueProto
import com.nevmem.qms.logger.Logger
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.network.data.RegisterResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import kotlin.coroutines.suspendCoroutine

internal class NetworkManagerImpl(
    private val logger: Logger
) : NetworkManager {

    data class UserIdentity(val email: String?, val password: String?)
    data class RegisterRequest(val name: String?, val surname: String?, val identity: UserIdentity?)
    data class User(val id: String?, val name: String?, val surname: String?, val email: String?)

    interface BackendService {
        @POST("/client/login")
        fun login(@Body body: UserIdentity?): Call<ClientApiProto.AuthResponse>

        @POST("/client/register")
        fun register(@Body body: RegisterRequest?): Call<Any>

        @POST("/client/get_user")
        fun getUser(@Header("session") session: String): Call<User>
    }

    interface FeaturesService {
        @GET("/config/56cf679c-beb3-4f64-ac63-b8d3697b9cc2")
        fun loadFeatures(): Call<Map<String, String>>
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor {
            logger.reportEvent("network_request_to",
                mapOf("url" to it.request().url().toString()))
            val response = it.proceed(it.request())
            logger.reportEvent("network_response_from",
                mapOf("url" to it.request().url().toString(), "code" to response.code()))
            response
        }
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://qms.nikitonsky.tk")
            .client(client)
            .build()
    }

    private val featuresRetrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://nevmem.com")
            .client(client)
            .build()
    }

    private val service by lazy { retrofit.create(BackendService::class.java) }
    private val featuresService by lazy { featuresRetrofit.create(FeaturesService::class.java) }

    override suspend fun fetchDataForInvite(invite: String): QueueProto.Queue {
        TODO("Not yet implemented")
    }

    override suspend fun loadFeatures(): Map<String, String> = suspendCoroutine { continuation ->
        featuresService.loadFeatures().enqueue(object : Callback<Map<String, String>> {
            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                continuation.resumeWith(Result.failure(t))
            }

            override fun onResponse(
                call: Call<Map<String, String>>,
                response: Response<Map<String, String>>
            ) {
                val body = response.body()
                if (response.code() == 200 && body != null) {
                    continuation.resumeWith(Result.success(body))
                } else {
                    continuation.resumeWith(Result.failure(IllegalStateException("Response code isn't 200 or body not present")))
                }
            }
        })
    }

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
        service.getUser(token).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                continuation.resumeWith(Result.failure(t))
            }

            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                val body = response.body()
                if (response.code() == 200 && body != null) {
                    try {
                        continuation.resumeWith(Result.success(body.toApiClass()))
                    } catch (exception: Exception) {
                        continuation.resumeWith(Result.failure(exception))
                    }
                } else {
                    continuation.resumeWith(Result.failure(IllegalStateException("Response code isn't 200 or body not present")))
                }
            }
        })
    }

    private fun User.toApiClass(): ClientApiProto.User {
        val builder = ClientApiProto.User.newBuilder()
        if (email != null) {
            builder.email = email
        }
        if (name != null) {
            builder.name = name
        }
        if (surname != null) {
            builder.surname = surname
        }
        return builder.build()
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
