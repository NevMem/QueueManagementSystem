package com.nevmem.qms.documents

sealed class Document {
    data class Passport(val series: String, val number: String) : Document()
    data class InternationalPassport(val number: String) : Document()
    data class TIN(val number: String) : Document()
    data class HealthInsurancePolicy(val number: String) : Document()
}
