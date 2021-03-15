package com.nevmem.qms.push.service

import org.springframework.stereotype.Service

@Service
class TokenStorageService { // TODO: Use DB for this shit
    private val tokens = mutableSetOf<String>()

    fun saveToken(token: String) {
        tokens.add(token)
    }

    fun fetchTokens(): List<String> {
        return tokens.toList()
    }
}
