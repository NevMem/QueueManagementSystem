package com.nevmem.qms.common.utils

import kotlinx.coroutines.delay
import kotlin.math.min

suspend fun infiniteRetry(block: suspend () -> Unit) {
    var attempt = 1
    while (true) {
        try {
            block()
        } catch (exception: Exception) {
            println(exception.message)
            delay(min(attempt * 1000L, 10000L))
            attempt += 1
        }
    }
}
