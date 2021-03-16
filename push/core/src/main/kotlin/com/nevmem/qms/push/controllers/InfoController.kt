package com.nevmem.qms.push.controllers

import com.nevmem.qms.push.service.TokenStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin("*")
@RestController
@RequestMapping("/api/info")
class InfoController @Autowired constructor(
    private val tokenStorageService: TokenStorageService
) {

    @RequestMapping("/countTokens")
    fun countTokens(): Int = tokenStorageService.fetchTokens().size
}
