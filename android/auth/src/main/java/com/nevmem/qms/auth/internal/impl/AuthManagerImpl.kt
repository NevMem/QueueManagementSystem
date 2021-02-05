package com.nevmem.qms.auth.internal.impl

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.*
import com.nevmem.qms.keyvalue.KeyValueStorage
import com.nevmem.qms.logger.Logger
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
    private val networkManager: NetworkManager,
    private val logger: Logger
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

    init {
        logger.debugLog("loaded token: $actualToken")
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
        logger.debugLog("AuthManagerImpl login")
        emit(LoginState.Processing)
        try {
            val response = networkManager.login(
                ClientApiProto.UserIdentity.newBuilder()
                    .setEmail(credentials.login)
                    .setPassword(credentials.password)
                    .build())
            actualToken = response
            logger.debugLog("AuthManagerImpl success")
            emit(LoginState.Success)
        } catch (exception: Exception) {
            logger.debugLog("AuthManagerImpl error")
            emit(LoginState.Error(exception.message ?: ""))
        }
    }

    override fun register(credentials: RegisterCredentials): Flow<RegisterState> = flow {
        logger.debugLog("AuthManagerImpl register")
        emit(RegisterState.Processing)
        try {
            val response = networkManager.register(
                ClientApiProto.RegisterRequest.newBuilder()
                    .setName(credentials.name)
                    .setSurname(credentials.lastName)
                    .setIdentity(
                        ClientApiProto.UserIdentity.newBuilder()
                            .setEmail(credentials.login)
                            .setPassword(credentials.password)
                            .build()
                    )
                    .build()
            )
            if (response is RegisterResponse.Success) {
                logger.debugLog("AuthManagerImpl register success")
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
            logger.debugLog("AuthManagerImpl register error")
            emit(RegisterState.Error("Not unique email"))
        } catch (exception: Exception) {
            emit(RegisterState.Error(exception.message ?: ""))
        }
    }

    override fun logout() {
        actualToken = null
    }
}
