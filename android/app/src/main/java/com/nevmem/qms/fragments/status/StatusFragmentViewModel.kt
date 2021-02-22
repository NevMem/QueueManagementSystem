package com.nevmem.qms.fragments.status

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nevmem.qms.status.QueueStatus
import com.nevmem.qms.status.StatusProvider
import com.nevmem.qms.suggests.Suggest
import com.nevmem.qms.suggests.SuggestsManager

class StatusFragmentViewModel(
    private val statusProvider: StatusProvider,
    private val suggestsManager: SuggestsManager
) : ViewModel(), StatusProvider.Listener, SuggestsManager.Listener {

    sealed class Content {
        data class Suggests(val suggests: List<Suggest>): Content()
        data class Status(val status: QueueStatus): Content()
    }

    private val mContent by lazy { MutableLiveData<Content>() }
    val content: LiveData<Content> by lazy { mContent }

    init {
        statusProvider.addListener(this)
        suggestsManager.addListener(this)
        updateContent()
    }

    override fun onCleared() {
        super.onCleared()
        statusProvider.removeListener(this)
        suggestsManager.removeListener(this)
    }

    override fun onStatusChanged() {
        updateContent()
    }

    override fun onSuggestsUpdated() {
        updateContent()
    }

    private fun updateContent() {
        val queueStatus = statusProvider.queueStatus
        if (queueStatus != null) {
            mContent.postValue(Content.Status(queueStatus))
        } else {
            mContent.postValue(Content.Suggests(suggestsManager.suggests))
        }
    }
}
