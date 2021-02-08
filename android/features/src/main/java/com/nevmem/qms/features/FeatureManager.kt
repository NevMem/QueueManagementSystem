package com.nevmem.qms.features

interface FeatureManager {
    interface Listener {
        fun onFeaturesUpdated()
    }

    fun overrideFeature(name: String, value: String?)

    fun getFeature(name: String): String?
    fun getFeature(name: String, defaultValue: String): String

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)
}
