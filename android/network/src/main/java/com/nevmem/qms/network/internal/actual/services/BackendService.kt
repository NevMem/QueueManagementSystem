package com.nevmem.qms.network.internal.actual.services

import com.nevmem.qms.ClientApiProto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

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
