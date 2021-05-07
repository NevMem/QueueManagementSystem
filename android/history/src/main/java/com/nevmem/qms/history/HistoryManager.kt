package com.nevmem.qms.history

import com.nevmem.qms.TicketProto
import kotlinx.coroutines.channels.ReceiveChannel

interface HistoryManager {
    val history: ReceiveChannel<TicketProto.TicketList>

    val resolvedHistory: ReceiveChannel<List<ResolvedTicket>>
}
