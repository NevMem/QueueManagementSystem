package com.nevmem.qms.scanner.internal.parser

class SimpleParser : Parser {
    override fun parse(payload: String): String? {
        if (payload.startsWith("invite: ", ignoreCase = true)) {
            return payload.replace("invite: ", "", ignoreCase = true)
        }
        return null
    }
}
