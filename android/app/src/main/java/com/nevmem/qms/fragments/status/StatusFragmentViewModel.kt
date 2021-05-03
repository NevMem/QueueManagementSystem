package com.nevmem.qms.fragments.status

import androidx.lifecycle.*
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.status.OperationStatus
import com.nevmem.qms.status.QueueStatus
import com.nevmem.qms.status.StatusProvider
import com.nevmem.qms.suggests.Suggest
import com.nevmem.qms.suggests.SuggestsManager
import com.nevmem.qms.ui.service.SmallServiceViewViewModelDelegate
import kotlinx.coroutines.flow.Flow

class StatusFragmentViewModel(
    private val statusProvider: StatusProvider,
    private val suggestsManager: SuggestsManager,
    private val authManager: AuthManager,
    private val networkManager: NetworkManager
) : ViewModel(), StatusProvider.Listener, SuggestsManager.Listener {

    sealed class Content {
        data class Suggests(val suggests: List<Suggest>): Content()
        data class Status(val status: QueueStatus): Content()
    }

    private val mContent by lazy { MutableLiveData<Content>() }
    val content: LiveData<Content> by lazy { mContent }

    private val mSmallServiceViewState = MediatorLiveData<SmallServiceViewViewModelDelegate.State>()
    val smallServiceViewState: LiveData<SmallServiceViewViewModelDelegate.State> = mSmallServiceViewState

    private var smallServiceInfoDelegate: SmallServiceViewViewModelDelegate? = null

    init {
        statusProvider.addListener(this)
        suggestsManager.addListener(this)
        updateContent()
    }

    override fun onCleared() {
        super.onCleared()
        statusProvider.removeListener(this)
        suggestsManager.removeListener(this)
        smallServiceInfoDelegate?.dismiss()
    }

    override fun onStatusChanged() {
        updateContent()
    }

    override fun onSuggestsUpdated() {
        updateContent()
    }

    fun handleLeave(): Flow<OperationStatus<Unit>> = statusProvider.leave()

    private fun updateContent() {
        val queueStatus = statusProvider.queueStatus
        smallServiceInfoDelegate?.apply {
            mSmallServiceViewState.removeSource(state)
            dismiss()
        }
        if (queueStatus != null) {
            val orgId = queueStatus.serviceInfo?.organizationId
            val serviceId = queueStatus.serviceInfo?.serviceId
            if (orgId != null && serviceId != null) {
                smallServiceInfoDelegate = SmallServiceViewViewModelDelegate(
                        authManager,
                        networkManager,
                        orgId,
                        serviceId,
                        viewModelScope).apply {
                    mSmallServiceViewState.addSource(state) {
                        mSmallServiceViewState.postValue(it)
                    }
                }
            }
            mContent.postValue(Content.Status(queueStatus))
        } else {
            mContent.postValue(Content.Suggests(suggestsManager.suggests))
        }
    }
}
