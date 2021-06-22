package com.nevmem.qms.onboarding

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.nevmem.qms.keyvalue.KeyValueStorage
import com.nevmem.qms.logger.Logger
import com.nevmem.qms.onboarding.internal.OnboardingDelegateImpl

fun createOnboardingDelegate(
    context: Context,
    logger: Logger,
    keyValueStorage: KeyValueStorage,
    fragmentManager: FragmentManager
) : OnboardingDelegate = OnboardingDelegateImpl(context, logger, keyValueStorage, fragmentManager)
