package com.nevmem.qms.documents

sealed class Document {
    data class Passport(val series: String, val number: String) : Document()
    data class InternationalPassport(val number: String) : Document()
    data class TIN(val number: String) : Document()
    data class HealthInsurancePolicy(val number: String) : Document()
}

val Document.name: String
    get() {
        return when (this) {
            is Document.TIN -> tinName
            is Document.Passport -> passportName
            is Document.HealthInsurancePolicy -> passportName
            is Document.InternationalPassport -> internationalPassportName
        }
    }
