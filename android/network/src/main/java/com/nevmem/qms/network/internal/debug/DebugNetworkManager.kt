package com.nevmem.qms.network.internal.debug

import com.google.gson.Gson
import com.nevmem.qms.QueueDescriptionProto
import com.nevmem.qms.network.NetworkManager
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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class DebugNetworkManager : NetworkManager {

    data class QueueDescription(var name: String, var description: String, var imageUrl: String?)

    private val gson = Gson()

    interface NetworkService {
        @Headers("Accept:application/json")
        @GET("/simple/{id}")
        fun fetchDataByInvite(@Path("id") invite: String): Call<String>
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl("https://nevmem.com")
        .build()

    private val backendService: NetworkService

    init {
        backendService = retrofit.create(NetworkService::class.java)
    }

    data class Description(var name: String? = null, var description: String? = null, var imageUrl: String? = null)

    override suspend fun fetchDataForInvite(invite: String): QueueDescriptionProto.QueueDescription = suspendCoroutine { continuation ->
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
                        val builder = QueueDescriptionProto.QueueDescription.newBuilder()
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
}
