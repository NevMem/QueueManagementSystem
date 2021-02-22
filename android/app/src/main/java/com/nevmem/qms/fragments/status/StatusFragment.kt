package com.nevmem.qms.fragments.status

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.qms.R
import com.nevmem.qms.recycler.BaseRecyclerAdapter
import com.nevmem.qms.status.QueueStatus
import com.nevmem.qms.suggests.Suggest
import kotlinx.android.synthetic.main.fragment_queue_status.*
import org.koin.android.viewmodel.ext.android.viewModel

class StatusFragment : Fragment(R.layout.fragment_queue_status) {

    private val model: StatusFragmentViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.content.observe(viewLifecycleOwner, Observer {
            updateUiWith(it)
        })
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
            SuggestsFactory(requireContext())
        )
    }

    private fun updateUi(queueStatus: QueueStatus) {
        hideSuggestsUi()
        statusCard.isVisible = true
        queueStatus.let {
            numberInLine.text = "Вы ${it.numberInLine}-ый в очереди"
            ticketNumber.text = it.ticket
            eta.text = "~${(it.etaInSeconds ?: 0 + 59) / 60} минут"
        }
    }

    private fun hideSuggestsUi() {
        suggestsView.isVisible = false
    }

    private fun hideStatusUi() {
        statusCard.isVisible = false
    }
}
