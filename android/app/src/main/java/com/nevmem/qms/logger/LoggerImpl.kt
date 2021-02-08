package com.nevmem.qms.logger

import com.nevmem.qms.BuildConfig
import com.yandex.metrica.YandexMetrica

class LoggerImpl : Logger {
    override fun reportEvent(eventName: String) {
        YandexMetrica.reportEvent(eventName)
    }

    override fun reportEvent(eventName: String, map: Map<String, Any>) {
        YandexMetrica.reportEvent(eventName, map)
    }

    override fun debugLog(value: String) {
        if (BuildConfig.DEBUG) {
            println("debug_output: $value")
        }
    }
}
