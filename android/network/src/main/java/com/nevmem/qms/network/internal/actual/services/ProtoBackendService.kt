package com.nevmem.qms.network.internal.actual.services

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.TicketProto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ProtoBackendService {
    @POST("/client/get_current_queue_info")
    fun currentTicketInfo(@Header("session") session: String): Call<TicketProto.TicketInfo>

    @POST("/client/left_queue")
    fun leave(@Header("session") session: String): Call<Unit>

    @POST("/client/tickets_history")
    fun history(@Header("session") session: String): Call<TicketProto.TicketList>

    @POST("/client/get_user")
    fun getUser(@Header("session") session: String): Call<ClientApiProto.User>

    @POST("/client/fetch_organization")
    @Headers("Content-Type: application/protobuf")
    fun getOrganization(
        @Body body: OrganizitionProto.OrganizationInfo
    ): Call<OrganizitionProto.Organization>

    @POST("/client/update_user")
    @Headers("Content-Type: application/protobuf")
    fun updateUser(@Header("session") session: String, @Body user: ClientApiProto.User): Call<Unit>
}
