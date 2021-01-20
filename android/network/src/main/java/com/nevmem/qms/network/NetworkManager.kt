package com.nevmem.qms.network

import com.nevmem.qms.QueueProto

interface NetworkManager {
    suspend fun fetchDataForInvite(invite: String): QueueProto.Queue

    suspend fun loadFeatures(): Map<String, String>
}
