package com.nevmem.qms.status.internal

import android.content.Context
import com.nevmem.qms.ServiceProto
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.common.utils.runOnUi
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.notifications.Channel
import com.nevmem.qms.notifications.Notification
import com.nevmem.qms.notifications.NotificationsManager
import com.nevmem.qms.status.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val updateTime = 1000L

internal class NetworkStatusProvider(
    private val context: Context,
    private val networkManager: NetworkManager,
    private val notificationsManager: NotificationsManager,
    private val authManager: AuthManager
) : StatusProvider {

    override var queueStatus: QueueStatus? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            showNotificationIfNeeded()
            runOnUi {
                notifyChanged()
            }
        }

    private val listeners = mutableSetOf<StatusProvider.Listener>()

    private val channel = Channel(
        "status-channel",
        context.getString(R.string.status),
        context.getString(R.string.time_of_appointment)
    )

    private var lastNotifiedTicket: String? = null

    init {
        notificationsManager.registerChannelIfNeeded(channel)

        GlobalScope.launch(Dispatchers.Default) {
            while (true) {
                queueStatus = try {
                    val info = networkManager.currentTicketInfo(authManager.token)
                    val newQueueStatus = QueueStatus(info)
                    newQueueStatus
                } catch (exception: Exception) {
                    null
                }

                delay(updateTime)
            }
        }
    }

    override fun join(serviceInfo: ServiceProto.ServiceInfo): Flow<JoinStatus> = flow {
        emit(JoinStatus.Pending)
        try {
            networkManager.join(authManager.token, serviceInfo)
            emit(JoinStatus.Success)
        } catch (exception: Exception) {
            emit(JoinStatus.Error(exception.message ?: ""))
        }
    }

    override fun fetchDataForInvite(invite: String): Flow<FetchStatus> = flow {
        emit(FetchStatus.Pending)
        try {
            val result = networkManager.fetchOrganization(authManager.token, invite)
            emit(FetchStatus.Success(result))
        } catch (ex: Exception) {
            emit(FetchStatus.Error(ex.message ?: context.getString(R.string.unknown_error)))
        }
    }

    override fun addListener(listener: StatusProvider.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: StatusProvider.Listener) {
        listeners.remove(listener)
    }

    private fun showNotificationIfNeeded() {
        queueStatus?.let { status ->
            if (status.etaInSeconds < 60 * 5 && lastNotifiedTicket != status.ticketId) {
                lastNotifiedTicket = status.ticketId
                notificationsManager.showNotificationInChannel(
                    channel.channelName,
                    Notification(
                        R.drawable.icon_status_notification,
                        context.getString(R.string.appointment_is_soon),
                        context.getString(R.string.appointment_is_soon_body)))
            }
        }
    }

    private fun notifyChanged() {
        listeners.forEach { it.onStatusChanged() }
    }
}
