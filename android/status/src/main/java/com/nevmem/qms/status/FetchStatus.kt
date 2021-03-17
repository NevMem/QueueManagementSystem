package com.nevmem.qms.status

import com.nevmem.qms.OrganizitionProto

sealed class FetchStatus {
    object Pending : FetchStatus()
    data class Error(val message: String) : FetchStatus()
    data class Success(val payload: OrganizitionProto.Organization): FetchStatus()
}
