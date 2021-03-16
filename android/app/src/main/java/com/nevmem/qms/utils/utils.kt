package com.nevmem.qms.utils

import com.nevmem.qms.BuildConfig

inline fun ifDebug(crossinline block: () -> Unit) {
    if (BuildConfig.DEBUG) {
        block()
    }
}
