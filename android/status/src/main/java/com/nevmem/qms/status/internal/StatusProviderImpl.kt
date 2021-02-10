package com.nevmem.qms.status.internal

import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.status.FetchStatus
import com.nevmem.qms.status.JoinStatus
import com.nevmem.qms.status.QueueStatus
import com.nevmem.qms.status.StatusProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

internal class StatusProviderImpl(private val networkManager: NetworkManager) : StatusProvider {
    override var queueStatus: QueueStatus? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            notifyChanged()
        }

    private val listeners = mutableSetOf<StatusProvider.Listener>()

    init {
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
                }
            }
        }
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

    override fun join(invite: String): Flow<JoinStatus> {
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
