package com.nevmem.qms.ui.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.ServiceProto
import com.nevmem.qms.organizations.OrganizationsRepo
import com.nevmem.qms.organizations.exceptions.makeServiceInfo
import com.nevmem.qms.organizations.exceptions.toOrganizationInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SmallServiceViewViewModelDelegate(
    private val organizationsRepo: OrganizationsRepo,
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
                val organization = organizationsRepo.findOrganization(organizationId.toOrganizationInfo())
                val service = organizationsRepo.findService(organizationId.makeServiceInfo(serviceId))
                mutableState.postValue(State.Data(organization.info, service))
            } catch (exception: Exception) {
                mutableState.postValue(State.Error(exception.message ?: ""))
            }
        }
    }

    fun dismiss() {
        job?.cancel()
    }
}
