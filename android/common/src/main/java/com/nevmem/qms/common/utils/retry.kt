package com.nevmem.qms.common.utils

import kotlinx.coroutines.delay
import kotlin.math.min

suspend fun infiniteRetry(block: suspend () -> Unit) {
    var attempt = 1
    while (true) {
        try {
            block()
            return
        } catch (exception: Exception) {
            delay(min(attempt * 1000L, 10000L))
            attempt += 1
        }
    }
}

suspend fun <T>retry(block: suspend () -> T, maxRetries: Int = 3): T {
    var attempt = 1
    while (attempt <= maxRetries) {
        try {
            return block()
        } catch (exception: Exception) {
            if (attempt == maxRetries) {
                throw exception
            }
            attempt += 1
        }
    }
    throw IllegalStateException("Something strange happened")
}

suspend fun <T>defaultRetry(block: suspend () -> T): T {
    return retry(block)
}
