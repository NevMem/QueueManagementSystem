package com.nevmem.qms.status

sealed class JoinStatus {
    object Pending : JoinStatus()
    data class Error(val message: String) : JoinStatus()
    object Success : JoinStatus()
}
