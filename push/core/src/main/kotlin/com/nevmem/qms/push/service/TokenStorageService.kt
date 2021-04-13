package com.nevmem.qms.push.service

import com.nevmem.qms.push.data.TokenWithEmail
import com.nevmem.qms.push.repository.TokensRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TokenStorageService @Autowired constructor(
    private val tokensRepository: TokensRepository
) {
    fun saveToken(email: String, token: String) {
        val saved = tokensRepository.findByEmail(email)
        if (saved != null) {
            saved.token = token
            tokensRepository.save(saved)
            return
        }
        tokensRepository.save(TokenWithEmail(email, token))
    }

    fun tokenByEmail(email: String): String? = tokensRepository.findByEmail(email)?.token

    fun fetchTokens(): List<String> = tokensRepository.findAll().map { it.token }
    fun fetchEmails(): List<String> = tokensRepository.findAll().map { it.email }
}
