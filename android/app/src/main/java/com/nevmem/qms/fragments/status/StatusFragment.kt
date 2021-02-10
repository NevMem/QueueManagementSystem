package com.nevmem.qms.fragments.status

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.qms.R
import com.nevmem.qms.status.QueueStatus
import kotlinx.android.synthetic.main.fragment_queue_status.*
import org.koin.android.viewmodel.ext.android.viewModel

class StatusFragment : Fragment(R.layout.fragment_queue_status) {

    private val model: StatusFragmentViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.queueStatus.observe(viewLifecycleOwner, Observer {
            updateUi(it)
        })
    }

    private fun updateUi(queueStatus: QueueStatus) {
        queueStatus?.let {
            numberInLine.text = "Вы ${it.numberInLine}-ый в очереди"
            ticketNumber.text = it.ticket
            eta.text = "~${(it.etaInSeconds ?: 0 + 59) / 60} минут"
        }
    }
}
