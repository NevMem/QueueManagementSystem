package com.nevmem.qms.auth.data

data class RegisterCredentials(
    var login: String,
    var password: String,
    var name: String,
    var lastName: String
)
