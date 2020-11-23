package com.nevmem.qms.status

import com.nevmem.qms.QueueDescriptionProto

sealed class FetchStatus {
    object Pending : FetchStatus()
    data class Error(val message: String) : FetchStatus()
    data class Success(val payload: QueueDescriptionProto.QueueDescription): FetchStatus()
}
