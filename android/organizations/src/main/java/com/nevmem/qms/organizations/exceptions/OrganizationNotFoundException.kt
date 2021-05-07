package com.nevmem.qms.organizations.exceptions

class OrganizationNotFoundException(internal val reason: Reason) : Exception() {
    enum class Reason {
        NetworkError,
        NotFound
    }

    override val message: String?
        get() {
            return when (reason) {
                Reason.NetworkError -> "Network error"
                Reason.NotFound -> "Not Found"
            }
        }
}
