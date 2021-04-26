package com.nevmem.qms.qmskit

object QMSKit {
    fun initialize() {
        System.loadLibrary("qmskit")
    }

    external fun apiKey(): String
}
