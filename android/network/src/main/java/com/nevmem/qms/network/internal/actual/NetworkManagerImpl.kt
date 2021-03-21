package com.nevmem.qms.network.internal.actual

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.ServiceProto
import com.nevmem.qms.data.NewPushTokenRequest
import com.nevmem.qms.logger.Logger
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.network.data.RegisterResponse
import com.nevmem.qms.network.internal.actual.services.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.suspendCoroutine

internal class NetworkManagerImpl(
    private val logger: Logger
) : NetworkManager {

    private val client = OkHttpClient.Builder()
        .addInterceptor {
            logger.reportEvent("network-manager.request-to",
                mapOf("url" to it.request().url().toString()))
            val response = it.proceed(it.request())
            logger.reportEvent("network-manager.from",
                mapOf("url" to it.request().url().toString(), "code" to response.code()))
            response
        }
        .build()

    private fun createDefaultRetrofitBuilder() = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)

    private val retrofit by lazy {
        createDefaultRetrofitBuilder()
            .baseUrl("http://qms-back.nikitonsky.tk")
            .build()
    }

    private val pushRetrofit by lazy {
        createDefaultRetrofitBuilder()
            .baseUrl("http://84.201.128.37:8002")
            .build()
    }

    private val featuresRetrofit by lazy {
        createDefaultRetrofitBuilder()
            .baseUrl("https://nevmem.com")
            .build()
    }

    private val service by lazy { retrofit.create(BackendService::class.java) }
    private val featuresService by lazy { featuresRetrofit.create(FeaturesService::class.java) }
    private val pushService by lazy { pushRetrofit.create(PushRegistrationService::class.java) }

    override suspend fun fetchDataForInvite(token: String, invite: String): OrganizitionProto.Organization = suspendCoroutine { continuation ->
        suspend fun wrap() = suspendCoroutine<Organization> { wrapRequest(service.getOrganization(token, OrganizationInfo(invite)), it) }
        GlobalScope.launch {
            try {
                val response = wrap()
                val organization = OrganizitionProto.Organization.newBuilder()
                    .setInfo(OrganizitionProto.OrganizationInfo.newBuilder()
                        .setName(response.info?.name ?: "")
                        .setAddress(response.info?.address ?: "")
                        .putAllData(response.info?.data ?: mapOf())
                        .build())
                    .addAllServices(response.services.map {
                        val service = ServiceProto.Service.newBuilder()
                            .setInfo(ServiceProto.ServiceInfo.newBuilder()
                                .setId(it.info.id)
                                .setOrganizationId(it.info.organizationId)
                                .setName(it.info.name)
                                .putAllData(it.info.data)
                                .build())
                            .build()
                        service
                    })
                    .build()
                continuation.resumeWith(Result.success(organization))
            } catch (t: Throwable) {
                continuation.resumeWith(Result.failure(t))
            }
        }
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

    override suspend fun registerNewPushToken(request: NewPushTokenRequest) = suspendCoroutine<Unit> { continuation ->
        pushService.registerNewPushToken(request).enqueue(object : Callback<Unit> {
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                continuation.resumeWith(Result.failure(t))
            }

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful && response.code() == 200) {
                    continuation.resumeWith(Result.success(Unit))
                } else {
                    continuation.resumeWith(Result.failure(IllegalStateException("Cannot register new token")))
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
