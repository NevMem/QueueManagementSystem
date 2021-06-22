package com.nevmem.qms

import android.app.Application
import android.content.Context
import com.nevmem.qms.auth.createAuthManager
import com.nevmem.qms.dialogs.createDialogsManager
import com.nevmem.qms.documents.createDocumentsManager
import com.nevmem.qms.features.createFeatureManager
import com.nevmem.qms.feedback.createFeedbackManager
import com.nevmem.qms.fragments.dev_settings.DeveloperSettingsFragmentViewModel
import com.nevmem.qms.fragments.login.LoginPageViewModel
import com.nevmem.qms.fragments.profile.ProfileFragmentViewModel
import com.nevmem.qms.fragments.registration.RegistrationPageViewModel
import com.nevmem.qms.fragments.status.StatusFragmentViewModel
import com.nevmem.qms.history.createHistoryManager
import com.nevmem.qms.keyvalue.createKeyValueStorage
import com.nevmem.qms.logger.Logger
import com.nevmem.qms.logger.LoggerImpl
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.network.createNetworkManager
import com.nevmem.qms.notifications.createNotificationsManager
import com.nevmem.qms.organizations.createOrganizationsRepo
import com.nevmem.qms.permissions.createPermissionsManager
import com.nevmem.qms.push.PushProcessor
import com.nevmem.qms.push.ToastPushProcessor
import com.nevmem.qms.qmskit.QMSKit
import com.nevmem.qms.rating.createRatingsManager
import com.nevmem.qms.status.StatusProvider
import com.nevmem.qms.status.createStatusProvider
import com.nevmem.qms.suggests.createDebugSuggestsManager
import com.nevmem.qms.toast.manager.ShowToastManager
import com.nevmem.qms.toast.manager.ToastManager
import com.nevmem.qms.toast.manager.ToastProvider
import com.nevmem.qms.toast.manager.createToastManager
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val AUTH_PREFS_NAME = "auth-prefs"
private const val FEATURES_PREFS_NAME = "features-prefs"
private const val ONBOARDING_PREFS_NAME = "onboarding-prefs"

class QMSApplication : Application() {

    private val appModule = module {
        single<ToastManager> { createToastManager() }
        single<ShowToastManager> { get<ToastManager>() }
        single<ToastProvider> { get<ToastManager>() }
        single<NetworkManager> { createNetworkManager(get()) }
        single<StatusProvider> { createStatusProvider(get(), get(), get(), get(), get()) }
        single {
            createFeatureManager(
                get(),
                createKeyValueStorage(getSharedPreferences(FEATURES_PREFS_NAME, Context.MODE_PRIVATE)),
                get())
        }
        single { createPermissionsManager() }
        single {
            createAuthManager(
                createKeyValueStorage(getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)),
                get(),
                get())
        }
        single<Logger> { LoggerImpl() }
        single { createDebugSuggestsManager() }
        single { createNotificationsManager(this@QMSApplication) }
        single<PushProcessor>(named("toast-push-processor")) {
            ToastPushProcessor(get())
        }
        single { createFeedbackManager(get(), get()) }
        single { createRatingsManager(get(), get()) }
        single { createDialogsManager() }
        single { createHistoryManager(get(), get(), get()) }
        single { createOrganizationsRepo(get()) }
        single { createDocumentsManager(get()) }
        single(named("onboarding-storage")) {
            createKeyValueStorage(getSharedPreferences(ONBOARDING_PREFS_NAME, Context.MODE_PRIVATE))
        }
    }

    private val viewModelsModule = module {
        viewModel { LoginPageViewModel(get()) }
        viewModel { ProfileFragmentViewModel(get(), get(), get(), get(), get()) }
        viewModel { RegistrationPageViewModel(get(), get()) }
        viewModel { DeveloperSettingsFragmentViewModel(get()) }
        viewModel { StatusFragmentViewModel(get(), get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        val config = YandexMetricaConfig
            .newConfigBuilder("e95eb26e-fac6-4c74-a352-2cc1d4b19ac4")
            .withLogs()
            .build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)

        QMSKit.initialize()

        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(appModule, viewModelsModule)
        }
    }
}
