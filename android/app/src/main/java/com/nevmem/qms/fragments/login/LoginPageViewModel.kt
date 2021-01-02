package com.nevmem.qms.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.LoginCredentials
import com.nevmem.qms.auth.data.LoginState
import com.yandex.metrica.YandexMetrica
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginPageViewModel(private val authManager: AuthManager) : ViewModel() {

    private val buttonInteractive = MutableLiveData<Boolean>(true)
    internal val loginButtonInteractive: LiveData<Boolean> = buttonInteractive

    private val error = MutableLiveData<String>()
    internal val loginErrors: LiveData<String> = error

    private val success = MutableLiveData<Boolean>()
    internal val loginSuccess: LiveData<Boolean> = success

    internal fun performLogin(login: String, password: String) {
        buttonInteractive.postValue(false)
        GlobalScope.launch {
            authManager.login(LoginCredentials(login, password)).collect {
                buttonInteractive.postValue(it !is LoginState.Processing)

                if (it is LoginState.Error) {
                    error.postValue(it.error)
                } else if (it is LoginState.Success) {
                    success.postValue(true)
                }

                YandexMetrica.reportEvent(
                    "login.doLogin", mapOf(
                        "state" to it.toString()
                    )
                )
            }
        }
    }
}
