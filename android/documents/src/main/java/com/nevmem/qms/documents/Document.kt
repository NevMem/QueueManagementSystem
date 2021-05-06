package com.nevmem.qms.documents

sealed class Document {
    data class Passport(val series: String, val number: String) : Document()
    data class InternationalPassport(val number: String) : Document()
    data class TIN(val number: String) : Document()
    data class HealthInsurancePolicy(val number: String) : Document()
}

enum class DocumentType {
    Passport, InternationalPassport, TIN, HealthInsurancePolicy
}

val Document.name: String
    get() {
        return when (this) {
            is Document.TIN -> tinName
            is Document.Passport -> passportName
            is Document.HealthInsurancePolicy -> healthInsurancePolicyName
            is Document.InternationalPassport -> internationalPassportName
        }
    }
