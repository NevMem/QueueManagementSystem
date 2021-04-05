package com.nevmem.qms.scanner.internal.parser

interface Parser {
    fun parse(payload: String): String?
}
