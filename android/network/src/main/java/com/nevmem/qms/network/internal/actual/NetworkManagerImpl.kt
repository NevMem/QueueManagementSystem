package com.nevmem.qms.network.internal.actual

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.ServiceProto
import com.nevmem.qms.TicketProto
import com.nevmem.qms.data.NewPushTokenRequest
import com.nevmem.qms.data.feedback.*
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
import retrofit2.converter.protobuf.ProtoConverterFactory
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

    private val protobufClient = OkHttpClient.Builder()
        .addInterceptor {
            val newRequest = it.request().newBuilder()
                .header("Content-Type", "application/protobuf")
                .build()
            it.proceed(newRequest)
        }
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

    private val protoRetrofit by lazy {
        Retrofit.Builder()
            .client(protobufClient)
            .addConverterFactory(ProtoConverterFactory.create())
            .baseUrl("http://qms-back.nikitonsky.tk")
            .build()
    }

    private val javaBackendRetrofit by lazy {
        createDefaultRetrofitBuilder()
            .baseUrl("https://nevmem.com/qms/")
            .build()
    }

    private val featuresRetrofit by lazy {
        createDefaultRetrofitBuilder()
            .baseUrl("https://nevmem.com")
            .build()
    }

    private val service by lazy { retrofit.create(BackendService::class.java) }
    private val protoService by lazy { protoRetrofit.create(ProtoBackendService::class.java) }
    private val featuresService by lazy { featuresRetrofit.create(FeaturesService::class.java) }
    private val javaBackendService by lazy { javaBackendRetrofit.create(JavaBackendService::class.java) }

    override suspend fun fetchOrganization(
        token: String, invite: String
    ): OrganizitionProto.Organization = suspendCoroutine { continuation ->
        wrapRequest(
            protoService.getOrganization(
                token,
                OrganizitionProto.OrganizationInfo.newBuilder().setId(invite).build()),
            continuation)
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
        wrapRequest(protoService.getUser(token), continuation)
    }

    override suspend fun registerNewPushToken(request: NewPushTokenRequest) = suspendCoroutine<Unit> { continuation ->
        javaBackendService.registerNewPushToken(request).enqueue(object : Callback<Unit> {
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

    override suspend fun publishFeedback(request: PublishFeedbackRequest, token: String) = suspendCoroutine<Unit> { continuation ->
        wrapRequest(javaBackendService.publishFeedback(request, token), continuation)
    }

    override suspend fun loadFeedback(entityId: String, token: String): List<Feedback> = suspendCoroutine { continuation ->
        wrapRequest(javaBackendService.loadFeedback(token, LoadFeedbacksRequest(entityId)), continuation)
    }

    override suspend fun loadRating(entityId: String, token: String): Float? = suspendCoroutine { continuation ->
        suspend fun wrap() = suspendCoroutine<LoadRatingResponse> {
            wrapRequest(javaBackendService.loadRating(token, LoadRatingRequest(entityId)), it)
        }
        GlobalScope.launch {
            try {
                val result = wrap()
                continuation.resumeWith(Result.success(result.rating))
            } catch (exception: Exception) {
                continuation.resumeWith(Result.failure(exception))
            }
        }
    }

    override suspend fun join(token: String, serviceInfo: ServiceProto.ServiceInfo) = suspendCoroutine<Unit> { continuation ->
        val info = ServiceInfo(serviceInfo.id, serviceInfo.name, serviceInfo.organizationId)
        wrapRequest(service.join(token, info), continuation)
    }

    override suspend fun currentTicketInfo(token: String): TicketProto.TicketInfo = suspendCoroutine { continuation ->
        wrapRequest(protoService.currentTicketInfo(token), continuation)
    }

    override suspend fun leaveQueue(token: String) = suspendCoroutine<Unit> { continuation ->
        wrapRequest(protoService.leave(token), continuation)
    }

    override suspend fun ticketList(token: String): TicketProto.TicketList = suspendCoroutine {
        wrapRequest(protoService.history(token), it)
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
