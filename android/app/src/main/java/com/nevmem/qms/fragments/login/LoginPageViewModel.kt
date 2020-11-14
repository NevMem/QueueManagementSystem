package com.nevmem.qms.fragments.login

import androidx.lifecycle.ViewModel
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.LoginCredentials
import com.nevmem.qms.auth.data.LoginState
import com.yandex.metrica.YandexMetrica
import kotlinx.coroutines.flow.*

class LoginPageViewModel(private val authManager: AuthManager) : ViewModel() {
    internal fun doLogin(login: String, password: String): Flow<LoginState> =
        authManager.login(LoginCredentials(login, password)).onEach {
            YandexMetrica.reportEvent("login.doLogin", mapOf(
                "state" to it.toString()
            ))
        }
}
