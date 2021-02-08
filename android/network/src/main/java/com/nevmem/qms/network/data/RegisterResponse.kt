package com.nevmem.qms.network.data

sealed class RegisterResponse {
    object NotUniqueLogin : RegisterResponse()
    object Success : RegisterResponse()
}
