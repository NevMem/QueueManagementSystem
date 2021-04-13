package com.nevmem.qms.push.repository

import com.nevmem.qms.push.data.TokenWithEmail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TokensRepository : JpaRepository<TokenWithEmail, Long> {
    fun findByEmail(email: String): TokenWithEmail?
}
