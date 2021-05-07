package com.nevmem.qms.usecase.user

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.avatar
import com.nevmem.qms.auth.changeAvatar
import com.nevmem.qms.common.operations.OperationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChangeUserAvatarUsecase(
    private val authManager: AuthManager
) {
    fun changeAvatar(
        newAvatarUrl: String
    ): Flow<OperationStatus<Unit>> = flow {
        emit(OperationStatus.Pending())
        val user = authManager.user()
        if (user == null) {
            emit(OperationStatus.Error("Missing user"))
            return@flow
        }
        val newUser = user.changeAvatar(newAvatarUrl)
        try {
            authManager.updateUser(newUser)
            emit(OperationStatus.Success(Unit))
        } catch (ex: Exception) {
            emit(OperationStatus.Error(ex.message ?: ""))
        }
    }
}
