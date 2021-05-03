package com.nevmem.qms.history

import com.nevmem.qms.TicketProto
import kotlinx.coroutines.channels.Channel

interface HistoryManager {
    val historyChannel: Channel<TicketProto.TicketList>
}
