package com.nevmem.qms.documents.parser

import com.google.gson.Gson
import com.nevmem.qms.documents.*

object DocumentsParser {

    private val gson = Gson()

    fun parse(data: Map<String, String>): List<Document> {
        return listOf(
            passportName to Document.Passport::class.java,
            internationalPassportName to Document.InternationalPassport::class.java,
            tinName to Document.TIN::class.java,
            healthInsurancePolicyName to Document.HealthInsurancePolicy::class.java
        ).mapNotNull {
            val repr = data[it.first]
            if (repr != null) {
                try {
                    gson.fromJson(repr, it.second)
                } catch(ex: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }
}
