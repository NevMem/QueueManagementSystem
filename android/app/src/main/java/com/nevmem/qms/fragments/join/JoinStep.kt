package com.nevmem.qms.fragments.join

import androidx.fragment.app.Fragment

interface JoinStep {
    fun createFragment(moveToNextStep: () -> Unit = {}): Fragment
}
