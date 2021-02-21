package com.nevmem.qms.status

import com.nevmem.qms.QueueProto
import kotlinx.coroutines.flow.Flow

interface StatusProvider {
    interface Listener {
        fun onStatusChanged()
    }

    val queueStatus: QueueStatus?

    /**
     * Internally will be using auth manager to provide some data about user
     */
    fun join(queue: QueueProto.Queue): Flow<JoinStatus>

    fun fetchDataForInvite(invite: String): Flow<FetchStatus>

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)
}
