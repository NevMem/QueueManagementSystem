package com.nevmem.qms.history

import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.ServiceProto
import com.nevmem.qms.TicketProto

data class ResolvedTicket(
    val ticket: TicketProto.Ticket,
    val organization: OrganizitionProto.Organization,
    val service: ServiceProto.Service
)
