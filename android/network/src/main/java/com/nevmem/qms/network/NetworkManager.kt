package com.nevmem.qms.network

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.QueueProto
import com.nevmem.qms.data.NewPushTokenRequest
import com.nevmem.qms.network.data.RegisterResponse

interface NetworkManager {
    suspend fun fetchDataForInvite(invite: String): OrganizitionProto.Organization

    suspend fun loadFeatures(): Map<String, String>

    suspend fun login(credentials: ClientApiProto.UserIdentity): String
    suspend fun register(credentials: ClientApiProto.RegisterRequest): RegisterResponse

    suspend fun getUser(token: String): ClientApiProto.User

    suspend fun registerNewPushToken(request: NewPushTokenRequest)
}
