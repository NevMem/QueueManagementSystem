package com.nevmem.qms.fragments.join.step

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.nevmem.qms.R
import com.nevmem.qms.fragments.join.JoinFragmentDirections
import com.nevmem.qms.fragments.join.JoinStep
import com.nevmem.qms.fragments.join.JoinUsecase
import com.nevmem.qms.status.FetchStatus
import com.nevmem.qms.status.JoinStatus
import com.nevmem.qms.status.StatusProvider
import com.nevmem.qms.toast.manager.ShowToastManager
import com.nevmem.qms.utils.runOnUi
import kotlinx.android.synthetic.main.fragment_step_do_join.*
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject

class DoJoinStep : JoinStep {
    override fun createFragment(): Fragment = DoJoinFragment()

    companion object {
        class DoJoinFragment : Fragment(R.layout.fragment_step_do_join) {

            private val usecase: JoinUsecase by inject()
            private val statusProvider: StatusProvider by inject()
            private val showToastManager: ShowToastManager by inject()

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                usecase.fetchStatus?.let { status ->
                    if (status is FetchStatus.Success) {
                        status.payload.imageUrl?.let { url ->
                            Glide.with(this)
                                .load(url)
                                .placeholder(R.drawable.join_placeholder)
                                .into(image)
                        }

                        image.isVisible = status.payload.imageUrl != null

                        inviteDescription.text = status.payload.description
                        inviteName.text = status.payload.name

                        joinButton.isEnabled = true
                        joinButton.setOnClickListener {
                            joinButton.isEnabled = false
                            runOnUi {
                                statusProvider.join(status.payload).collect {
                                    when (it) {
                                        is JoinStatus.Pending -> {}
                                        is JoinStatus.Success -> {
                                            joinButton.isEnabled = true
                                            findNavController().navigate(JoinFragmentDirections.moveToStatusPage())
                                        }
                                        is JoinStatus.Error -> {
                                            joinButton.isEnabled = true
                                            showToastManager.error(it.message)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
