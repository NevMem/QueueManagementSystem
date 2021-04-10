package com.nevmem.qms.network

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.ServiceProto
import com.nevmem.qms.TicketProto
import com.nevmem.qms.data.NewPushTokenRequest
import com.nevmem.qms.data.feedback.Feedback
import com.nevmem.qms.data.feedback.PublishFeedbackRequest
import com.nevmem.qms.network.data.RegisterResponse

interface NetworkManager {
    suspend fun fetchOrganization(token: String, invite: String): OrganizitionProto.Organization

    suspend fun join(token: String, serviceInfo: ServiceProto.ServiceInfo)
    suspend fun currentTicketInfo(token: String): TicketProto.TicketInfo

    suspend fun loadFeatures(): Map<String, String>

    suspend fun login(credentials: ClientApiProto.UserIdentity): String
    suspend fun register(credentials: ClientApiProto.RegisterRequest): RegisterResponse

    suspend fun getUser(token: String): ClientApiProto.User

    suspend fun registerNewPushToken(request: NewPushTokenRequest)

    suspend fun publishFeedback(request: PublishFeedbackRequest, token: String)
    suspend fun loadFeedback(entityId: String, token: String): List<Feedback>
    suspend fun loadRating(entityId: String, token: String): Float?
}
