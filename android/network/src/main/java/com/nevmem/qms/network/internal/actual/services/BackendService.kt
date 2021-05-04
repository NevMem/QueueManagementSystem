package com.nevmem.qms.network.internal.actual.services

import com.nevmem.qms.ClientApiProto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class UserIdentity(val email: String?, val password: String?)
data class RegisterRequest(val name: String?, val surname: String?, val identity: UserIdentity?)
data class User(val id: String?, val name: String?, val surname: String?, val email: String?)
data class OrganizationInfo(
    var id: String = "", var name: String = "", var address: String = "", var data: Map<String, String> = mapOf())
data class ServiceInfo(
    var id: String = "", var name: String = "", var organizationId: String = "", var data: Map<String, String> = mapOf())
data class Service(var info: ServiceInfo)
data class Organization(var info: OrganizationInfo? = null, var services: List<Service> = listOf())
enum class TicketState {
    WAITING, PROCESSING, PROCESSED
}
data class Ticket(
    var state: TicketState? = null,
    var id: String = "",
    var userId: String = "",
    var serviceId: String = "",
    var ticketId: String = "",
    var organizationId: String = "",
    var window: String = "")
data class TicketInfo(
    var ticket: Ticket? = null,
    var remainingTime: Int?,
    var peopleInFront: Int?)

interface BackendService {
    @POST("/client/login")
    fun login(@Body body: UserIdentity?): Call<ClientApiProto.AuthResponse>

    @POST("/client/register")
    fun register(@Body body: RegisterRequest?): Call<Any>

    @POST("/client/fetch_organization")
    fun getOrganization(@Header("session") session: String, @Body body: OrganizationInfo): Call<Organization>

    @POST("/client/enter_queue")
    fun join(@Header("session") session: String, @Body body: ServiceInfo): Call<Unit>

    @POST("/client/get_current_queue_info")
    fun currentTicketInfo(@Header("session") session: String): Call<TicketInfo>
}
