package com.nevmem.qms.fragments.status

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.qms.R
import com.nevmem.qms.TicketProto
import com.nevmem.qms.dialogs.DialogsManager
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.recycler.BaseRecyclerAdapter
import com.nevmem.qms.status.QueueStatus
import com.nevmem.qms.suggests.Suggest
import kotlinx.android.synthetic.main.fragment_queue_status.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class StatusFragment : Fragment(R.layout.fragment_queue_status) {

    private val model: StatusFragmentViewModel by viewModel()
    private val featureManager: FeatureManager by inject()
    private val dialogsManager: DialogsManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.content.observe(viewLifecycleOwner, Observer {
            updateUiWith(it)
        })

        model.smallServiceViewState.observe(viewLifecycleOwner, Observer {
            serviceInfo.state = it
        })

        leaveButton.setOnClickListener {
            GlobalScope.launch {
                val resolution = dialogsManager.showSimpleDialog(
                    requireContext().getString(R.string.ready_to_leave))
                if (resolution == DialogsManager.SimpleDialogResolution.Ok) {
                    val flow = model.handleLeave()
                    dialogsManager.showOperationStatusDialog(flow)
                }
            }
        }
    }

    private fun updateUiWith(content: StatusFragmentViewModel.Content) {
        when (content) {
            is StatusFragmentViewModel.Content.Suggests -> updateUi(content.suggests)
            is StatusFragmentViewModel.Content.Status -> updateUi(content.status)
        }
    }

    private fun updateUi(suggests: List<Suggest>) {
        hideStatusUi()
        suggestsView.isVisible = true
        suggestsView.adapter = BaseRecyclerAdapter(
            listOf(HeaderItem) + suggests.map { SuggestItem(it) },
            HeaderFactory(requireContext()),
            SuggestsFactory(requireContext(), featureManager)
        )
    }

    private fun updateUi(queueStatus: QueueStatus) {
        hideSuggestsUi()
        statusCard.isVisible = true

        listOf(
            ::updateNumberInLine,
            ::updateTicketId,
            ::updateEta,
            ::updateLeaveButton
        ).forEach {
            it(queueStatus)
        }
    }

    private fun updateNumberInLine(queueStatus: QueueStatus) {
        if (queueStatus.ticketState == TicketProto.Ticket.State.WAITING) {
            numberInLine.isVisible = true
            numberInLine.text = resources.getQuantityString(
                R.plurals.numberInTheLine, queueStatus.numberInLine, queueStatus.numberInLine)
        } else {
            numberInLine.isVisible = false
        }
    }

    private fun updateTicketId(queueStatus: QueueStatus) {
        if (queueStatus.ticketState == TicketProto.Ticket.State.WAITING
            || queueStatus.ticketState == TicketProto.Ticket.State.PROCESSING) {
            ticketNumber.isVisible = true
            ticketNumber.text = queueStatus.ticketId
        } else {
            ticketNumber.isVisible = false
        }
    }

    private fun updateEta(queueStatus: QueueStatus) {
        if (queueStatus.ticketState == TicketProto.Ticket.State.WAITING) {
            eta.isVisible = true
            eta.text = resources.getString(
                R.string.minutes_remaining, (queueStatus.etaInSeconds + 59) / 60)
        } else {
            eta.isVisible = false
        }
    }

    private fun updateLeaveButton(queueStatus: QueueStatus) {
        val state = queueStatus.ticketState
        leaveButton.isVisible = false
        when (state) {
            TicketProto.Ticket.State.WAITING, TicketProto.Ticket.State.PROCESSING -> {
                leaveButton.isVisible = true
            }
            else -> {}
        }
    }

    private fun hideSuggestsUi() {
        suggestsView.isVisible = false
    }

    private fun hideStatusUi() {
        statusCard.isVisible = false
    }
}
