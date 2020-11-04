package com.nevmem.qms

import android.app.Application
import com.nevmem.qms.auth.createDebugAuthManager
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class QMSApplication : Application() {

    private val appModule = module {
        single { createDebugAuthManager() }
    }

    override fun onCreate() {
        super.onCreate()
        val config = YandexMetricaConfig
            .newConfigBuilder("e95eb26e-fac6-4c74-a352-2cc1d4b19ac4")
            .withLogs()
            .build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)

        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(appModule)
        }
    }
}
