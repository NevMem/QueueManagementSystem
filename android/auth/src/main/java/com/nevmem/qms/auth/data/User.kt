package com.nevmem.qms.auth.data

import com.nevmem.qms.ClientApiProto

sealed class UserLoadingState {

    object Pending : UserLoadingState()
    class Error(val message: String) : UserLoadingState()

    class User(
        var name: String,
        var surname: String,
        var email: String
    ) : UserLoadingState() {
        companion object {
            fun fromProto(user: ClientApiProto.User) = User(
                user.name.orEmpty(),
                user.surname.orEmpty(),
                user.email.orEmpty()
            )
        }
    }
}
