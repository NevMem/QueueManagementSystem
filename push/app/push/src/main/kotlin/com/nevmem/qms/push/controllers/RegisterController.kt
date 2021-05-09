package com.nevmem.qms.push.controllers

import com.nevmem.qms.data.NewPushTokenRequest
import com.nevmem.qms.commonclient.client.BackendClient
import com.nevmem.qms.push.service.TokenStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClientResponseException

@RestController
@CrossOrigin("*")
@RequestMapping("/api/push")
class RegisterController @Autowired constructor(
    private val storageService: TokenStorageService,
    private val client: BackendClient
) {

    @PostMapping("/register")
    fun registerWithToken(
        @RequestHeader("session") session: String,
        @RequestBody body: NewPushTokenRequest
    ) {
        val email = try {
            client.checkAuth(session)?.email
        } catch (exception: WebClientResponseException) {
            null
        }
        email?.let {
            storageService.saveToken(it, body.token)
        }
    }

}
