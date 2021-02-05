package com.nevmem.qms.logger

interface Logger {
    fun reportEvent(eventName: String)
    fun reportEvent(eventName: String, map: Map<String, Any>)

    fun debugLog(value: String)
}
