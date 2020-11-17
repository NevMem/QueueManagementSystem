package com.nevmem.qms.fragments.join.step

import androidx.fragment.app.Fragment
import com.nevmem.qms.R
import com.nevmem.qms.fragments.join.JoinStep

class InviteStep : JoinStep {
    override fun createFragment(moveToNextStep: () -> Unit): Fragment = InviteFragment(moveToNextStep)

    companion object {
        class InviteFragment(private val moveToNextStep: () -> Unit) : Fragment(R.layout.fragment_step_invite) {

        }
    }
}
