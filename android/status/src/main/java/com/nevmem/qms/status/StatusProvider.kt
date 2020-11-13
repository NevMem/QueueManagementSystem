package com.nevmem.qms.status

interface StatusProvider {
    interface Listener {
        fun onStatusChanged()
    }

    val queueStatus: QueueStatus?

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)
}
