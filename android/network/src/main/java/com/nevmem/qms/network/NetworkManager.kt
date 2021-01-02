package com.nevmem.qms.network

import com.nevmem.qms.QueueDescriptionProto

interface NetworkManager {
    suspend fun fetchDataForInvite(invite: String): QueueDescriptionProto.QueueDescription
}
