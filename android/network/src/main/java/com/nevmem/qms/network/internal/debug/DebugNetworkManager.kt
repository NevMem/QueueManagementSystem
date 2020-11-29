package com.nevmem.qms.network.internal.debug

import com.nevmem.qms.QueueDescriptionProto
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.network.exceptions.BodyNotPresentException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class DebugNetworkManager : NetworkManager {

    data class QueueDescription(var name: String, var description: String, var imageUrl: String?)

    interface NetworkService {
        @Headers("Accept:application/json")
        @GET("/simple/{id}")
        fun fetchDataByInvite(@Path("id") invite: String): Call<QueueDescription>
    }

    private val retrofit = Retrofit.Builder()
        // .addConverterFactory(ProtoConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://nevmem.com")
        .build()

    private val backendService: NetworkService

    init {
        backendService = retrofit.create(NetworkService::class.java)
    }

    override suspend fun fetchDataForInvite(invite: String): QueueDescriptionProto.QueueDescription = suspendCoroutine { continuation ->
        GlobalScope.launch(Dispatchers.IO) {
            backendService.fetchDataByInvite(invite).enqueue(object : Callback<QueueDescription> {
                override fun onFailure(call: Call<QueueDescription>, t: Throwable) {
                    continuation.resumeWith(Result.failure(t))
                }

                override fun onResponse(
                    call: Call<QueueDescription>,
                    response: Response<QueueDescription>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        val builder = QueueDescriptionProto.QueueDescription.newBuilder()
                            .setName(body.name)
                            .setDescription(body.description)
                        body.imageUrl?.let { builder.setImageUrl(it) }
                        continuation.resume(builder.build())
                    } else {
                        continuation.resumeWith(Result.failure(BodyNotPresentException()))
                    }
                }
            })
        }
    }
}
