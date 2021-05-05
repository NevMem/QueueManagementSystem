package com.nevmem.qms.history

import com.nevmem.qms.TicketProto
import kotlinx.coroutines.channels.Channel

interface HistoryManager {
    val history: Channel<TicketProto.TicketList>

    val resolvedHistory: Channel<List<ResolvedTicket>>
}
