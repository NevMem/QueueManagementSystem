package com.nevmem.qms.fragments.status

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nevmem.qms.status.QueueStatus
import com.nevmem.qms.status.StatusProvider

class StatusFragmentViewModel(
    private val statusProvider: StatusProvider
) : ViewModel(), StatusProvider.Listener {

    private val status by lazy { MutableLiveData<QueueStatus>() }
    val queueStatus: LiveData<QueueStatus> by lazy { status }

    init {
        statusProvider.addListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        statusProvider.removeListener(this)
    }

    override fun onStatusChanged() {
        statusProvider.queueStatus?.let { queueStatus ->
            status.postValue(queueStatus)
        }
    }
}
