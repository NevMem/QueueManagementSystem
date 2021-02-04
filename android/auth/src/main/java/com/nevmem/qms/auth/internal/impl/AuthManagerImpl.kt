package com.nevmem.qms.auth.internal.impl

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.*
import com.nevmem.qms.keyvalue.KeyValueStorage
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.network.data.RegisterResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val AUTH_MANAGER_TOKEN_KEY_NAME = "token_key"

internal class AuthManagerImpl(
    private val storage: KeyValueStorage,
    private val networkManager: NetworkManager
) : AuthManager {

    private var actualToken: String? = storage.getValue(AUTH_MANAGER_TOKEN_KEY_NAME)
        set (value) {
            if (field == value) {
                return
            }
            field = value
            if (field != null) {
                storage.setValue(AUTH_MANAGER_TOKEN_KEY_NAME, field!!)
            } else {
                storage.removeKey(AUTH_MANAGER_TOKEN_KEY_NAME)
            }
        }

    override val token: String
        get() {
            return actualToken!!
        }

    override val authenticationStatus: Channel<AuthenticationStatus>
        get() {
            return Channel<AuthenticationStatus>().apply {
                GlobalScope.launch {
                    send(AuthenticationStatus.Pending)
                    if (actualToken != null) {
                        send(AuthenticationStatus.LoggedIn)
                    } else {
                        send(AuthenticationStatus.Unauthorized)
                    }
                }
            }
        }

    override fun login(credentials: LoginCredentials): Flow<LoginState> = flow {
        emit(LoginState.Processing)
        try {
            val response = networkManager.login(
                ClientApiProto.UserIdentity.newBuilder()
                    .setEmail(credentials.login)
                    .setPassword(credentials.password)
                    .build())
            actualToken = response
            emit(LoginState.Success)
        } catch (exception: Exception) {
            emit(LoginState.Error(exception.message ?: ""))
        }
    }

    override fun register(credentials: RegisterCredentials): Flow<RegisterState> = flow {
        emit(RegisterState.Processing)
        val response = networkManager.register(ClientApiProto.RegisterRequest.newBuilder()
            .setName(credentials.name)
            .setSurname(credentials.lastName)
            .setIdentity(ClientApiProto.UserIdentity.newBuilder()
                .setEmail(credentials.login)
                .setPassword(credentials.password)
                .build())
            .build())
        if (response is RegisterResponse.Success) {
            login(LoginCredentials(credentials.login, credentials.password))
                .collect {
                    if (it is LoginState.Success) {
                        emit(RegisterState.Success)
                    } else if (it is LoginState.Error) {
                        emit(RegisterState.Error(it.error))
                    }
                }
            return@flow
        }
        emit(RegisterState.Error("Not unique email"))
    }

    override fun logout() {
        actualToken = null
    }
}
