package com.nevmem.qms.network.internal.debug

import com.google.gson.Gson
import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.QueueProto
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.network.data.RegisterResponse
import com.nevmem.qms.network.exceptions.BodyNotPresentException
import com.nevmem.qms.network.exceptions.UnusualResponseCodeException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import kotlin.coroutines.suspendCoroutine

internal class DebugNetworkManager : NetworkManager {

    data class QueueDescription(var name: String, var description: String, var imageUrl: String?)

    private val gson = Gson()

    interface NetworkService {
        @Headers("Accept:application/json")
        @GET("/simple/{id}")
        fun fetchDataByInvite(@Path("id") invite: String): Call<String>
    }

    interface NetworkWithJsonService {
        @GET("/simple/auh")
        fun fetchFeaturesMap(): Call<Map<String, String>>
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl("https://nevmem.com")
        .build()

    private val retrofitWithJson = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://nevmem.com")
        .build()

    private val backendService: NetworkService
    private val backendJsonService: NetworkWithJsonService

    init {
        backendService = retrofit.create(NetworkService::class.java)
        backendJsonService = retrofitWithJson.create(NetworkWithJsonService::class.java)
    }

    data class Description(var name: String? = null, var description: String? = null, var imageUrl: String? = null)

    override suspend fun loadFeatures(): Map<String, String> = suspendCoroutine { continuation ->
        GlobalScope.launch(Dispatchers.IO) {
            backendJsonService.fetchFeaturesMap().enqueue(object : Callback<Map<String, String>> {
                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    continuation.resumeWith(Result.failure(t))
                }

                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>
                ) {
                    val body = response.body()
                    if (response.code() != 200) {
                        continuation.resumeWith(Result.failure(UnusualResponseCodeException(response.code())))
                    } else if (!response.isSuccessful || body == null) {
                        continuation.resumeWith(Result.failure(BodyNotPresentException()))
                    } else {
                        continuation.resumeWith(Result.success(body))
                    }
                }
            })
        }
    }

    override suspend fun fetchDataForInvite(invite: String): QueueProto.Queue = suspendCoroutine { continuation ->
        GlobalScope.launch(Dispatchers.IO) {
            backendService.fetchDataByInvite(invite).enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    continuation.resumeWith(Result.failure(t))
                }

                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    val body = response.body()
                    if (response.code() != 200) {
                        continuation.resumeWith(Result.failure(UnusualResponseCodeException(response.code())))
                    } else if (response.isSuccessful && body != null) {
                        val result: Description = gson.fromJson(body, Description::class.java)
                        val builder = QueueProto.Queue.newBuilder()
                        result.apply {
                            name?.let { builder.setName(it) }
                            description?.let { builder.setDescription(it) }
                            imageUrl?.let { builder.setImageUrl(it) }
                        }
                        val res = builder.build()
                        continuation.resumeWith(Result.success(res))
                    } else {
                        continuation.resumeWith(Result.failure(BodyNotPresentException()))
                    }
                }
            })
        }
    }

    override suspend fun getUser(token: String): ClientApiProto.User {
        TODO("Not yet implemented")
    }

    override suspend fun login(credentials: ClientApiProto.UserIdentity): String {
        TODO("Not yet implemented")
    }

    override suspend fun register(credentials: ClientApiProto.RegisterRequest): RegisterResponse {
        TODO("Not yet implemented")
    }
}
