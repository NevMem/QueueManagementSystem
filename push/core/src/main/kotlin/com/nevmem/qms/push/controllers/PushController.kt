package com.nevmem.qms.push.controllers

import com.nevmem.qms.push.data.SendToOneRequest
import com.nevmem.qms.push.data.SpecifiedBroadcastMessageConfig
import com.nevmem.qms.push.service.FbPushService
import com.nevmem.qms.push.service.TokenStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/broadcast")
class PushController @Autowired constructor(
    private val storageService: TokenStorageService,
    private val fbService: FbPushService
) {
    @PostMapping("/specified")
    fun specified(@RequestBody body: SpecifiedBroadcastMessageConfig) {
        fbService.broadcast(
            body.tokens,
            body.data,
            body.notificationConfig)
    }

    @PostMapping("/one")
    fun sendToOne(@RequestBody body: SendToOneRequest) {
        val token = storageService.tokenByEmail(body.email)
        fbService.sendToOne(token!!, body.data, body.notificationConfig)
    }
}
