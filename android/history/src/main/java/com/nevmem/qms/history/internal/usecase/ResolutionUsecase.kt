package com.nevmem.qms.history.internal.usecase

import com.nevmem.qms.TicketProto
import com.nevmem.qms.history.ResolvedTicket
import com.nevmem.qms.organizations.OrganizationsRepo
import com.nevmem.qms.organizations.exceptions.makeServiceInfo
import com.nevmem.qms.organizations.exceptions.toOrganizationInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

internal class ResolutionUsecase(
    private val coroutineScope: CoroutineScope,
    private val organizationsRepo: OrganizationsRepo
) {
    suspend fun resolveTicket(
        ticket: TicketProto.Ticket
    ): ResolvedTicket = suspendCoroutine { continuation ->
        coroutineScope.launch {
            try {
                val organization = organizationsRepo.findOrganization(
                    ticket.organizationId.toOrganizationInfo())
                val service = organizationsRepo.findService(
                    ticket.organizationId.makeServiceInfo(ticket.serviceId))
                continuation.resumeWith(Result.success(ResolvedTicket(ticket, organization, service)))
            } catch (ex: Exception) {
                continuation.resumeWith(Result.failure(ex))
            }
        }
    }
}
