package com.nevmem.qms.usecase.user

import com.nevmem.qms.auth.AuthManager

class ChangeUserAvatarUsecaseFactory(
    private val authManager: AuthManager
) {
    fun createUsecase(): ChangeUserAvatarUsecase = ChangeUserAvatarUsecase(authManager)
}
