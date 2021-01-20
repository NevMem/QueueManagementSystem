package com.nevmem.qms.status

import com.nevmem.qms.QueueProto

sealed class FetchStatus {
    object Pending : FetchStatus()
    data class Error(val message: String) : FetchStatus()
    data class Success(val payload: QueueProto.Queue): FetchStatus()
}
