package com.nevmem.qms.auth.data

import com.nevmem.qms.ClientApiProto

class User(
    var name: String,
    var surname: String,
    var email: String
) {
    companion object {
        fun fromProto(user: ClientApiProto.User) = User(
            user.name.orEmpty(),
            user.surname.orEmpty(),
            user.email.orEmpty()
        )
    }
}
