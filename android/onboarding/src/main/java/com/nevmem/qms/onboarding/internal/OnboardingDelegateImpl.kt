package com.nevmem.qms.onboarding.internal

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.nevmem.qms.keyvalue.KeyValueStorage
import com.nevmem.qms.logger.Logger
import com.nevmem.qms.onboarding.OnboardingDelegate
import com.nevmem.qms.onboarding.internal.data.OnboardingItem
import com.nevmem.qms.onboarding.internal.ui.OnboardingDialogFragment

internal class OnboardingDelegateImpl(
    private val context: Context,
    private val logger: Logger,
    private val keyValueStorage: KeyValueStorage,
    private val fragmentManager: FragmentManager
) : OnboardingDelegate {

    private val storage = OnboardingItemsStorage(context, logger, keyValueStorage)

    override fun startOnboardingIfNeeded() {
        val items = storage.provideItems()
        if (items.isEmpty()) {
            return
        }
        showDialog(items.first())
    }

    private fun showDialog(item: OnboardingItem) {
        val fragment = OnboardingDialogFragment.newInstance(item) { it, closedViaButton ->
            storage.reportItemShown(it)
            if (closedViaButton) {
                startOnboardingIfNeeded()
            }
        }
        fragment.show(fragmentManager, "onboarding")
    }
}
