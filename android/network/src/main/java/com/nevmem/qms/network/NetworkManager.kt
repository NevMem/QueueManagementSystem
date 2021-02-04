package com.nevmem.qms.network

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.QueueProto
import com.nevmem.qms.network.data.RegisterResponse

interface NetworkManager {
    suspend fun fetchDataForInvite(invite: String): QueueProto.Queue

    suspend fun loadFeatures(): Map<String, String>

    suspend fun login(credentials: ClientApiProto.UserIdentity): String
    suspend fun register(credentials: ClientApiProto.RegisterRequest): RegisterResponse
}
