package com.nevmem.qms.onboarding.internal

import android.content.Context
import com.nevmem.qms.keyvalue.KeyValueStorage
import com.nevmem.qms.logger.Logger
import com.nevmem.qms.onboarding.R
import com.nevmem.qms.onboarding.internal.data.OnboardingItem

internal class OnboardingItemsStorage(
    private val context: Context,
    private val logger: Logger,
    private val keyValueStorage: KeyValueStorage
) {
    fun provideItems(): List<OnboardingItem> {
        return allItems().filter {
            !keyValueStorage.hasKey(it.itemId)
        }
    }

    fun reportItemShown(item: OnboardingItem) {
        logger.reportEvent("onboarding-shown", mapOf(
            "id" to item.itemId
        ))
        keyValueStorage.setValue(item.itemId, "shown")
    }

    private fun allItems(): List<OnboardingItem> {
        return listOf(
            OnboardingItem("onboarding-app-info", context.getString(R.string.onboarding_app_info))
        )
    }
}
