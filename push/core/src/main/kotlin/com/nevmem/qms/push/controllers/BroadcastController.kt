package com.nevmem.qms.push.controllers

import com.nevmem.qms.push.service.FbPushService
import com.nevmem.qms.push.service.TokenStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class BroadcastController @Autowired constructor(
    private val storageService: TokenStorageService,
    private val fbService: FbPushService
) {

    data class BroadcastMessageConfig(var message: String = "")

    @PostMapping("/broadcast")
    fun broadcast(@RequestBody body: BroadcastMessageConfig) {
        fbService.broadcast(
            storageService.fetchTokens(),
            body.message)
    }

}
