package com.nevmem.qms.network.internal.actual.services

import com.nevmem.qms.TicketProto
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface ProtoBackendService {
    @POST("/client/get_current_queue_info")
    fun currentTicketInfo(@Header("session") session: String): Call<TicketProto.TicketInfo>

    @POST("/client/left_queue")
    fun leave(@Header("session") session: String): Call<Unit>

    @POST("/client/tickets_history")
    fun history(@Header("session") session: String): Call<TicketProto.TicketList>
}
