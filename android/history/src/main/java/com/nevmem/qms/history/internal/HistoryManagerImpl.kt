package com.nevmem.qms.history.internal

import com.nevmem.qms.TicketProto
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.common.utils.infiniteRetry
import com.nevmem.qms.history.HistoryManager
import com.nevmem.qms.network.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class HistoryManagerImpl(
    private val authManager: AuthManager,
    private val networkManager: NetworkManager
) : HistoryManager {
    override val historyChannel: Channel<TicketProto.TicketList> = Channel(Channel.CONFLATED)

    init {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                infiniteRetry {
                    val ticketsList = networkManager.ticketList(authManager.token)
                    historyChannel.send(ticketsList)
                }

                delay(10_000L)
            }
        }
    }
}
