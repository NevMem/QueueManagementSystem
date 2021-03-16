package com.nevmem.qms.push.controllers

import com.nevmem.qms.push.data.BroadcastMessageConfig
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
class BroadcastController @Autowired constructor(
    private val storageService: TokenStorageService,
    private val fbService: FbPushService
) {

    @PostMapping("/all")
    fun broadcast(@RequestBody body: BroadcastMessageConfig) {
        fbService.broadcast(
            storageService.fetchTokens(),
            body.data,
            body.notificationConfig)
    }

    @PostMapping("/specified")
    fun specified(@RequestBody body: SpecifiedBroadcastMessageConfig) {
        fbService.broadcast(
            body.tokens,
            body.data,
            body.notificationConfig)
    }

}
