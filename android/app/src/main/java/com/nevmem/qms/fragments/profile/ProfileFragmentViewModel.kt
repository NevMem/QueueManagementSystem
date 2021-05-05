package com.nevmem.qms.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.common.operations.OperationStatus
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.features.isFeatureEnabled
import com.nevmem.qms.history.HistoryManager
import com.nevmem.qms.knownfeatures.KnownFeatures
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.toast.manager.ShowToastManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileFragmentViewModel(
    private val authManager: AuthManager,
    private val showToastManager: ShowToastManager,
    private val historyManager: HistoryManager,
    featureManager: FeatureManager
) : ViewModel() {

    private val profileList = MutableLiveData<List<RVItem>>()
    internal val profile: LiveData<List<RVItem>> = profileList

    private val visitedList = MutableLiveData<List<RVItem>>()
    internal val visited: LiveData<List<RVItem>> = visitedList

    private var user: ClientApiProto.User? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            updateData()
        }

    init {
        profileList.postValue(listOf(ProfileLoadingStub))
        viewModelScope.launch {
            authManager.currentUser().collect {
                if (it is OperationStatus.Error) {
                    showToastManager.error(it.message)
                } else if (it is OperationStatus.Success) {
                    user = it.result
                }
            }
        }
        if (featureManager.isFeatureEnabled(KnownFeatures.ShowHistoryOnProfilePage.value)) {
            viewModelScope.launch {
                val history = historyManager.resolvedHistory.receive()
                visitedList.postValue(history.map {
                    ProfileVisitedPlace(it.organization, it.service)
                })
            }
        }
    }

    private fun updateData() {
        profileList.postValue(mutableListOf<RVItem>().apply {
            add(ProfileAvatar(user?.dataMap?.get("avatar")))
            addIf(user!!.name != null, ProfileName(user!!.name!!))
            addIf(user!!.surname != null, ProfileLastName(user!!.surname!!))
            addIf(user!!.email != null, ProfileEmail(user!!.email!!))
            add(ProfileRating(4.92))
            add(ProfileDocument(DocumentType.Passport, "9214 775590"))
            add(ProfileDocument(DocumentType.InternationalPassport, "9214775590"))
            add(ProfileDocument(DocumentType.Policy, "*******9999"))
            add(ProfileAddDocument)
        })
    }

    private fun<T, U : T> MutableList<T>.addIf(ifValue: Boolean, value: U) {
        if (ifValue) {
            add(value)
        }
    }
}
