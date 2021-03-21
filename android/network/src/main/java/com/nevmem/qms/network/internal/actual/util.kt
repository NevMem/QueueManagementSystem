package com.nevmem.qms.network.internal.actual

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.Continuation

fun<T> wrapRequest(call: Call<T>, continuation: Continuation<T>) {
    call.enqueue(object: Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            continuation.resumeWith(Result.failure(t))
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val body = response.body()
            if (response.code() == 200 && body != null) {
                continuation.resumeWith(Result.success(body))
            } else {
                continuation.resumeWith(Result.failure(IllegalStateException("Response code isn't 200 or body is empty")))
            }
        }
    })
}
