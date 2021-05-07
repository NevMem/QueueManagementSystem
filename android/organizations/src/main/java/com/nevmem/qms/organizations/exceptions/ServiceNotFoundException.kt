package com.nevmem.qms.organizations.exceptions

class ServiceNotFoundException(private val reason: Reason) : Exception() {
    enum class Reason {
        NetworkError,
        NotFound,
        NotEnoughInfo
    }

    override val message: String?
        get() {
            return when(reason) {
                Reason.NetworkError -> "Network error"
                Reason.NotEnoughInfo -> "Not enough info"
                Reason.NotFound -> "Not found"
            }
        }
}
