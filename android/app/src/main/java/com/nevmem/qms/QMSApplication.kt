package com.nevmem.qms

import android.app.Application
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class QMSApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = YandexMetricaConfig
            .newConfigBuilder("e95eb26e-fac6-4c74-a352-2cc1d4b19ac4")
            .withLogs()
            .build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }
}
