package com.nevmem.qms.fragments.status

import androidx.fragment.app.Fragment
import com.nevmem.qms.R
import com.nevmem.qms.status.QueueStatus
import com.nevmem.qms.status.StatusProvider
import kotlinx.android.synthetic.main.fragment_queue_status.*
import org.koin.android.ext.android.inject

class StatusFragment : Fragment(R.layout.fragment_queue_status), StatusProvider.Listener {

    private val statusProvider: StatusProvider by inject()

    private var queueStatus: QueueStatus? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            updateUi()
        }

    override fun onResume() {
        super.onResume()
        statusProvider.addListener(this)
        onStatusChanged()
    }

    override fun onPause() {
        super.onPause()
        statusProvider.removeListener(this)
    }

    override fun onStatusChanged() {
        queueStatus = statusProvider.queueStatus
    }

    private fun updateUi() {
        queueStatus?.let {
            numberInLine.text = "Вы ${it.numberInLine}-ый в очереди"
            ticketNumber.text = it.ticket
            eta.text = "~${(it.etaInSeconds + 59) / 60} минут"
        }
    }
}
