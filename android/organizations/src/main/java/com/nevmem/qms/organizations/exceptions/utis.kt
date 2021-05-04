package com.nevmem.qms.organizations.exceptions

import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.ServiceProto

fun String.toOrganizationInfo(): OrganizitionProto.OrganizationInfo =
    OrganizitionProto.OrganizationInfo.newBuilder()
        .setId(this)
        .build()

fun String.makeServiceInfo(serviceId: String)
    = ServiceProto.ServiceInfo.newBuilder()
        .setId(serviceId)
        .setOrganizationId(this)
        .build()
