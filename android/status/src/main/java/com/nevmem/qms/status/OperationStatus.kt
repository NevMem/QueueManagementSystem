package com.nevmem.qms.status

sealed class OperationStatus<T : Any> {
    class Pending<T : Any>: OperationStatus<T>()
    data class Error<T : Any>(val message: String) : OperationStatus<T>()
    data class Success<T : Any>(val result: T) : OperationStatus<T>()
}
