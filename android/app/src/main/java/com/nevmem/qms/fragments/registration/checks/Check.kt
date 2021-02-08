package com.nevmem.qms.fragments.registration.checks

interface Check {
    sealed class Result {
        object Ok : Result()
        data class Error(val message: String): Result()
    }

    suspend fun check(value: String?): Result
}
