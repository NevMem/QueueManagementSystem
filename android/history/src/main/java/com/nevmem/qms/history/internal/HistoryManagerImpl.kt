package com.nevmem.qms.history.internal

import com.nevmem.qms.TicketProto
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.common.utils.infiniteRetry
import com.nevmem.qms.history.HistoryManager
import com.nevmem.qms.history.ResolvedTicket
import com.nevmem.qms.history.internal.usecase.ResolutionUsecaseFactory
import com.nevmem.qms.network.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class HistoryManagerImpl(
    private val authManager: AuthManager,
    private val networkManager: NetworkManager,
    private val resolutionUsecaseFactory: ResolutionUsecaseFactory
) : HistoryManager {
    override val history: Channel<TicketProto.TicketList> = Channel(Channel.CONFLATED)

    override val resolvedHistory: Channel<List<ResolvedTicket>> = Channel(Channel.CONFLATED)

    init {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                infiniteRetry {
                    val ticketsList = networkManager.ticketList(authManager.token)
                    history.send(ticketsList)

                    val resolvedList = ticketsList.ticketsList.mapNotNull {
                        val usecase = resolutionUsecaseFactory.createUsecase()
                        try {
                            usecase.resolveTicket(it)
                        } catch (ex: Exception) {
                            null
                        }
                    }
                    resolvedHistory.send(resolvedList)
                }

                delay(10_000L)
            }
        }
    }
}
