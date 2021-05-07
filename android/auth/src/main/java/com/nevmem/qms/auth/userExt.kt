package com.nevmem.qms.auth

import com.nevmem.qms.ClientApiProto

val ClientApiProto.User.avatar: String?
    get() {
        return dataMap["avatar"]
    }

fun ClientApiProto.User.changeAvatar(avatar: String): ClientApiProto.User {
    return ClientApiProto.User.newBuilder(this)
        .putData("avatar", avatar)
        .build()
}
