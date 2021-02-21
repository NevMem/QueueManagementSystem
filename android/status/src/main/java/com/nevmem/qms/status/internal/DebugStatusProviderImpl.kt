package com.nevmem.qms.status.internal

import com.nevmem.qms.QueueProto
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.notifications.Channel
import com.nevmem.qms.notifications.Notification
import com.nevmem.qms.notifications.NotificationsManager
import com.nevmem.qms.status.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

internal class DebugStatusProviderImpl(
    private val notificationsManager: NotificationsManager
) : StatusProvider {
    override var queueStatus: QueueStatus? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            notifyChanged()
        }

    private val channel = Channel(
        "status-channel",
        "Status",
        "Time to appointment"
    )

    private val listeners = mutableSetOf<StatusProvider.Listener>()

    init {
        notificationsManager.registerChannelIfNeeded(channel)

        GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                queueStatus = null
                notifyChanged()

                val ticketNumber = Random().nextInt() % 100
                val ticket = "T$ticketNumber"

                val avgTime = (abs(Random().nextInt()) % 5 + 2) * 60

                var numberInLine = (abs(Random().nextInt()) % 10) + 5

                queueStatus = QueueStatus(numberInLine, ticket, numberInLine * avgTime, "")

                repeat(numberInLine) {
                    delay(1000)
                    numberInLine -= 1
                    queueStatus = QueueStatus(numberInLine, ticket, numberInLine * avgTime, "")
                    if (numberInLine * avgTime < 5 * 60) {
                        notificationsManager.showNotificationInChannel(channel.id, Notification(
                            R.drawable.icon_status_notification,
                            "Time to your appointment",
                            "${(numberInLine * avgTime + 59) / 60} min"
                        ))
                    }
                }
            }
        }
    }

    override fun fetchDataForInvite(invite: String): Flow<FetchStatus> = flow {

    }

    override fun join(queue: QueueProto.Queue): Flow<JoinStatus> {
        TODO("Not yet implemented")
    }

    override fun addListener(listener: StatusProvider.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: StatusProvider.Listener) {
        listeners.remove(listener)
    }

    private fun notifyChanged() {
        listeners.forEach { it.onStatusChanged() }
    }
}
