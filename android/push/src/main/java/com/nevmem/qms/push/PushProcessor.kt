package com.nevmem.qms.push

interface PushProcessor {
    fun onPushData(data: Map<String, String>)
}
