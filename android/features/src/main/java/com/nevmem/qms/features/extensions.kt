package com.nevmem.qms.features

fun FeatureManager.isFeatureEnabled(key: String): Boolean = getFeature(key) == "enabled"
