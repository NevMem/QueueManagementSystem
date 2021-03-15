package com.nevmem.qms.push.controllers

import com.nevmem.qms.data.NewPushTokenRequest
import com.nevmem.qms.push.service.TokenStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
@RequestMapping("/api/push")
class RegisterController @Autowired constructor(
    private val storageService: TokenStorageService
) {

    @PostMapping("/register")
    fun registerWithToken(@RequestBody body: NewPushTokenRequest) {
        storageService.saveToken(body.token)
    }

}
