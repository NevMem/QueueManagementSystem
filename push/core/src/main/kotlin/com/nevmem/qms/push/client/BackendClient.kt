package com.nevmem.qms.push.client

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class BackendClient {

    private val baseUrl = "https://qms-back.nikitonsky.tk"
    private val webClient = WebClient.create(baseUrl)

    data class CheckAuthResp(var email: String = "")

    fun checkAuth(token: String): CheckAuthResp? {
        val req = webClient.post()
        req.uri("/check_auth")
        req.header("session", token)
        val response = req.retrieve()
        return response.bodyToMono(CheckAuthResp::class.java).block()
    }

    final inline fun <reified T>withCheckAuth(token: String, block: () -> T): ResponseEntity<T> {
        try {
            checkAuth(token)
        } catch (exception: WebClientResponseException) {
            return ResponseEntity.status(exception.statusCode).build()
        }
        return ResponseEntity.ok(block())
    }
}
