package com.nevmem.qms.keyvalue

interface KeyValueStorage {
    fun hasKey(key: String): Boolean
    fun getValue(key: String): String?
    fun getValue(key: String, notFound: String): String
    fun setValue(key: String, value: String)

    fun keys(): List<String>
}
