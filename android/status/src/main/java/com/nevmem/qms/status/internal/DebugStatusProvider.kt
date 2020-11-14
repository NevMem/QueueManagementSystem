package com.nevmem.qms.status.internal

import com.nevmem.qms.status.QueueStatus
import com.nevmem.qms.status.StatusProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

internal class DebugStatusProvider : StatusProvider {
    override var queueStatus: QueueStatus? = null

    private val listeners = mutableSetOf<StatusProvider.Listener>()

    init {
        GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                queueStatus = null
                notifyChanged()

                delay(500)

                val ticketNumber = Random().nextInt() % 100
                val ticket = "T$ticketNumber"

                val avgTime = (abs(Random().nextInt()) % 5 + 2) * 60

                var numberInLine = (abs(Random().nextInt()) % 10) + 5

                queueStatus = QueueStatus(numberInLine, ticket, numberInLine * avgTime, "")
                notifyChanged()

                repeat(numberInLine) {
                    delay(1000)
                    numberInLine -= 1
                    queueStatus = QueueStatus(numberInLine, ticket, numberInLine * avgTime, "")
                    notifyChanged()
                }
            }
        }
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
