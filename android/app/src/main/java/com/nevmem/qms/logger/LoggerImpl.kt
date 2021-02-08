package com.nevmem.qms.logger

import com.nevmem.qms.utils.ifDebug
import com.yandex.metrica.YandexMetrica

class LoggerImpl : Logger {
    override fun reportEvent(eventName: String) {
        YandexMetrica.reportEvent(eventName)
    }

    override fun reportEvent(eventName: String, map: Map<String, Any>) {
        YandexMetrica.reportEvent(eventName, map)
    }

    override fun debugLog(value: String) {
        ifDebug {
            println("debug_output: $value")
        }
    }
}
