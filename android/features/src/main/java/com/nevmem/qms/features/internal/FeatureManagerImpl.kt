package com.nevmem.qms.features.internal

import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.keyvalue.KeyValueStorage
import com.nevmem.qms.network.NetworkManager
import com.yandex.metrica.YandexMetrica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val updateDelay = 5000L

internal class FeatureManagerImpl(
    private val networkManager: NetworkManager,
    private val storage: KeyValueStorage
) : FeatureManager {

    private val listeners = mutableSetOf<FeatureManager.Listener>()

    private val features = mutableMapOf<String, String>()

    init {
        storage.keys().forEach { key ->
            storage.getValue(key)?.let { features[key] = it }
        }
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    val newFeatures = networkManager.loadFeatures()
                    launch(Dispatchers.Main) {
                        features.clear()
                        features.putAll(newFeatures)
                        newFeatures.keys.forEach { key ->
                            storage.setValue(key, newFeatures.getValue(key))
                        }
                        listeners.forEach { it.onFeaturesUpdated() }
                    }
                    YandexMetrica.reportEvent("update-features", mapOf(
                        "type" to "ok"
                    ))
                } catch(exception: Exception) {
                    YandexMetrica.reportEvent("update-features", mapOf(
                        "type" to "error",
                        "reason" to exception.message
                    ))
                }
                delay(updateDelay)
            }
        }
    }

    override fun getFeature(name: String): String? {
        return features[name]
    }

    override fun getFeature(name: String, defaultValue: String): String {
        return getFeature(name) ?: defaultValue
    }

    override fun addListener(listener: FeatureManager.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: FeatureManager.Listener) {
        listeners.remove(listener)
    }
}
