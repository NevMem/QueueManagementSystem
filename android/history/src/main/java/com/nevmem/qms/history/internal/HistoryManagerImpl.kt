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
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class HistoryManagerImpl(
    private val authManager: AuthManager,
    private val networkManager: NetworkManager,
    private val resolutionUsecaseFactory: ResolutionUsecaseFactory
) : HistoryManager {
    private val mHistory = BroadcastChannel<TicketProto.TicketList>(CONFLATED)
    override val history: ReceiveChannel<TicketProto.TicketList> get() = mHistory.openSubscription()

    private val mResolvedHistory = BroadcastChannel<List<ResolvedTicket>>(CONFLATED)
    override val resolvedHistory: ReceiveChannel<List<ResolvedTicket>> get() = mResolvedHistory.openSubscription()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                infiniteRetry {
                    val ticketsList = networkManager.ticketList(authManager.token)
                    mHistory.send(ticketsList)

                    val resolvedList = ticketsList.ticketsList.mapNotNull {
                        val usecase = resolutionUsecaseFactory.createUsecase()
                        try {
                            usecase.resolveTicket(it)
                        } catch (ex: Exception) {
                            null
                        }
                    }
                    mResolvedHistory.send(resolvedList)
                }

                delay(10_000L)
            }
        }
    }
}
