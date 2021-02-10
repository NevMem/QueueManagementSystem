package com.nevmem.qms.status

data class QueueStatus(
    val numberInLine: Int?,
    val ticket: String?,
    val etaInSeconds: Int?,
    val schemeId: String?
)
