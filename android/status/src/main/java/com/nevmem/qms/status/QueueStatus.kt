package com.nevmem.qms.status

import com.nevmem.qms.TicketProto

data class QueueStatus(
    private val ticketInfo: TicketProto.TicketInfo
) {
    data class ServiceInfo(val organizationId: String, val serviceId: String)

    val ticketId by lazy {
        ticketInfo.ticket.ticketId ?: ""
    }

    val etaInSeconds by lazy {
        ticketInfo.remainingTime
    }

    val serviceInfo by lazy {
        val serviceId = ticketInfo.ticket.serviceId
        val orgId = ticketInfo.ticket.organizationId
        if (serviceId != null && orgId != null) {
            ServiceInfo(orgId, serviceId)
        } else {
            null
        }
    }

    val numberInLine by lazy {
        ticketInfo.peopleInFrontCount + 1
    }

    val ticketState: TicketProto.Ticket.State by lazy {
        ticketInfo.ticket.state
    }

    val resolution: TicketProto.Ticket.Resolution by lazy {
        ticketInfo.ticket.resolution
    }

    val window: String?
        get() = ticketInfo.ticket.window
}
