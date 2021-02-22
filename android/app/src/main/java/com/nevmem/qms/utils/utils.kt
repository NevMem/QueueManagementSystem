package com.nevmem.qms.utils

import com.nevmem.qms.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

inline fun ifDebug(crossinline block: () -> Unit) {
    if (BuildConfig.DEBUG) {
        block()
    }
}

inline fun runOnUi(crossinline block: suspend () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        block()
    }
}
