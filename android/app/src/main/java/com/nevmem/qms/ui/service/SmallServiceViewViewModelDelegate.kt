package com.nevmem.qms.ui.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.ServiceProto
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SmallServiceViewViewModelDelegate(
    private val authManager: AuthManager,
    private val networkManager: NetworkManager,
    private val organizationId: String,
    private val serviceId: String,
    viewModelScope: CoroutineScope
) {
    sealed class State {
        object None : State()
        object Loading : State()
        data class Error(val message: String) : State()
        data class Data(val organizationInfo: OrganizitionProto.OrganizationInfo, val service: ServiceProto.Service) : State()
    }

    private val mutableState = MutableLiveData<State>()
    val state: LiveData<State> = mutableState

    private var job: Job? = null

    init {
        job = viewModelScope.launch {
            mutableState.postValue(State.Loading)
            try {
                val org = networkManager.fetchOrganization(authManager.token, organizationId)
                val service = org.servicesList.find { it.info.id == serviceId }
                if (service != null) {
                    mutableState.postValue(State.Data(org.info, service))
                }
            } catch (exception: Exception) {
                mutableState.postValue(State.Error(exception.message ?: ""))
            }
        }
    }

    fun dismiss() {
        job?.cancel()
    }
}
