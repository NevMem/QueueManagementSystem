package com.nevmem.qms.status.internal

import com.nevmem.qms.QueueProto
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.notifications.NotificationsManager
import com.nevmem.qms.status.FetchStatus
import com.nevmem.qms.status.JoinStatus
import com.nevmem.qms.status.QueueStatus
import com.nevmem.qms.status.StatusProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class NetworkStatusProvider(
    private val networkManager: NetworkManager,
    private val notificationsManager: NotificationsManager
) : StatusProvider {

    private val listeners = mutableSetOf<StatusProvider.Listener>()

    override val queueStatus: QueueStatus?
        get() = TODO("Not yet implemented")

    override fun join(queue: QueueProto.Queue): Flow<JoinStatus> {
        TODO("Not yet implemented")
    }

    override fun fetchDataForInvite(invite: String): Flow<FetchStatus> = flow {
        emit(FetchStatus.Pending)
        try {
            val result = networkManager.fetchDataForInvite(invite)
            emit(FetchStatus.Success(result))
        } catch (ex: Exception) {
            emit(FetchStatus.Error(ex.message ?: "Unknown error"))
        }
    }

    override fun addListener(listener: StatusProvider.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: StatusProvider.Listener) {
        listeners.remove(listener)
    }
}
