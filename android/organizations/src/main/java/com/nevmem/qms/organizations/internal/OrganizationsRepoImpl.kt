package com.nevmem.qms.organizations.internal

import androidx.collection.LruCache
import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.ServiceProto
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.organizations.OrganizationsRepo
import com.nevmem.qms.organizations.exceptions.OrganizationNotFoundException
import com.nevmem.qms.organizations.exceptions.ServiceNotFoundException
import com.nevmem.qms.organizations.exceptions.toOrganizationInfo

internal class OrganizationsRepoImpl(
    private val networkManager: NetworkManager
) : OrganizationsRepo {
    override suspend fun findOrganization(
        organizationInfo: OrganizitionProto.OrganizationInfo
    ): OrganizitionProto.Organization {
        cache.get(organizationInfo.id)?.let {
            return it
        }
        val response = networkManager.fetchOrganization(organizationInfo)
        cache.put(organizationInfo.id, response)
        return response
    }

    override suspend fun findService(
        serviceInfo: ServiceProto.ServiceInfo
    ): ServiceProto.Service {
        if (serviceInfo.organizationId == null) {
            throw ServiceNotFoundException(ServiceNotFoundException.Reason.NotEnoughInfo)
        }
        val orgInfo = serviceInfo.organizationId.toOrganizationInfo()
        val organization = try {
            findOrganization(orgInfo)
        } catch (exception: OrganizationNotFoundException) {
            when (exception.reason) {
                OrganizationNotFoundException.Reason.NotFound
                    -> throw ServiceNotFoundException(ServiceNotFoundException.Reason.NotFound)
                OrganizationNotFoundException.Reason.NetworkError
                    -> throw ServiceNotFoundException(ServiceNotFoundException.Reason.NetworkError)
            }
        }
        return organization.servicesList.find { it.info.id == serviceInfo.id }
            ?: throw ServiceNotFoundException(ServiceNotFoundException.Reason.NotFound)
    }

    private val cache = LruCache<String, OrganizitionProto.Organization>(64)
}
