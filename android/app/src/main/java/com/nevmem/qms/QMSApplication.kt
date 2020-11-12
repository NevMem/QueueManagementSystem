package com.nevmem.qms

import android.app.Application
import com.nevmem.qms.auth.createDebugAuthManager
import com.nevmem.qms.fragments.login.LoginFragment
import com.nevmem.qms.fragments.login.LoginPageViewModel
import com.nevmem.qms.model.toast.ShowToastManager
import com.nevmem.qms.model.toast.ToastManager
import com.nevmem.qms.model.toast.ToastProvider
import com.nevmem.qms.model.toast.createToastManager
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class QMSApplication : Application() {

    private val appModule = module {
        single { createDebugAuthManager() }
        single<ToastManager> { createToastManager() }
        single<ShowToastManager> { get<ToastManager>() }
        single<ToastProvider> { get<ToastManager>() }
        viewModel { LoginPageViewModel(get()) }
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
