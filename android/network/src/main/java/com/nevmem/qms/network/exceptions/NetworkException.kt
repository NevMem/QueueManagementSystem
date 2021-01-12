package com.nevmem.qms.network.exceptions

sealed class NetworkException(message: String) : Exception(message)

class BodyNotPresentException : NetworkException("Body not present")
class UnusualResponseCodeException(code: Int) : NetworkException("Unusual response code $code")

