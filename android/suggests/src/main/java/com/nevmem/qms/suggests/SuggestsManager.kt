package com.nevmem.qms.suggests

interface SuggestsManager {
    interface Listener {
        fun onSuggestsUpdated()
    }

    val suggests: List<Suggest>

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)
}
