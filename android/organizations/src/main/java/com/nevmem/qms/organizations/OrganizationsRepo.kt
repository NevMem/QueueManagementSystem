package com.nevmem.qms.organizations

import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.ServiceProto

interface OrganizationsRepo {
    suspend fun findOrganization(
        organizationInfo: OrganizitionProto.OrganizationInfo
    ): OrganizitionProto.Organization

    suspend fun findService(
        serviceInfo: ServiceProto.ServiceInfo
    ): ServiceProto.Service
}
