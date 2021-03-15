package com.nevmem.qms.common.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

inline fun runOnUi(crossinline block: suspend () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        block()
    }
}
