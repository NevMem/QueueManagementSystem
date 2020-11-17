package com.nevmem.qms.status

sealed class FetchStatus {
    object Pending : FetchStatus()
    data class Error(val message: String) : FetchStatus()
    data class Success(val payload: String): FetchStatus()
}
