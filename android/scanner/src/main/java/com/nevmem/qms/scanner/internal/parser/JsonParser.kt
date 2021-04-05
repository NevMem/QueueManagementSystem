package com.nevmem.qms.scanner.internal.parser

import org.json.JSONObject

class JsonParser : Parser {
    override fun parse(payload: String): String? {
        return try {
            val obj = JSONObject(payload)
            obj.getString("organization")
        } catch (exception: Exception) {
            null
        }
    }
}
