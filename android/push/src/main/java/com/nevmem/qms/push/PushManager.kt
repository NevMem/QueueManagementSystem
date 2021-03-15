package com.nevmem.qms.push

interface PushManager {
    fun addPushProcessor(processor: PushProcessor)
    fun removePushProcessor(processor: PushProcessor)

    fun processDataFromIntent(data: Map<String, String>)
}
