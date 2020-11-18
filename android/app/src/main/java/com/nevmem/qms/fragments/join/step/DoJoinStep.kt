package com.nevmem.qms.fragments.join.step

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nevmem.qms.R
import com.nevmem.qms.fragments.join.JoinStep
import com.nevmem.qms.fragments.join.JoinUsecase
import com.nevmem.qms.status.FetchStatus
import kotlinx.android.synthetic.main.fragment_step_do_join.*
import org.koin.android.ext.android.inject

class DoJoinStep : JoinStep {
    override fun createFragment(): Fragment = DoJoinFragment()

    companion object {
        class DoJoinFragment : Fragment(R.layout.fragment_step_do_join) {

            private val usecase: JoinUsecase by inject()

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                usecase.fetchStatus?.let { status ->
                    if (status is FetchStatus.Success) {
                        inviteDescription.text = status.payload.description
                    }
                }
            }
        }
    }
}
