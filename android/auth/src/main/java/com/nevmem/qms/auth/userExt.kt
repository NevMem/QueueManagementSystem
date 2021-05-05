package com.nevmem.qms.auth

import com.nevmem.qms.ClientApiProto

var ClientApiProto.User.avatar: String?
    get() {
        return dataMap["avatar"]
    }
    set(value) {
        dataMap["avatar"] = value
    }
