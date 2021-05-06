package com.nevmem.qms.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.avatar
import com.nevmem.qms.common.operations.OperationStatus
import com.nevmem.qms.documents.Document
import com.nevmem.qms.documents.DocumentsManager
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
    private val documentsManager: DocumentsManager,
    featureManager: FeatureManager
) : ViewModel() {

    private val profileList = MutableLiveData<List<RVItem>>()
    internal val profile: LiveData<List<RVItem>> = profileList

    private val profileDocuments = MutableLiveData<List<RVItem>>()
    internal val documents: LiveData<List<RVItem>> = profileDocuments

    private val visitedList = MutableLiveData<List<RVItem>>()
    internal val visited: LiveData<List<RVItem>> = visitedList

    private var user: ClientApiProto.User? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            updateData()
            updateDocuments()
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
            add(ProfileAvatar(user?.avatar))
            addIf(user!!.name != null, ProfileName(user!!.name!!))
            addIf(user!!.surname != null, ProfileLastName(user!!.surname!!))
            addIf(user!!.email != null, ProfileEmail(user!!.email!!))
            add(ProfileRating(4.92))
        })
    }

    private fun updateDocuments() {
        viewModelScope.launch {
            val documents = documentsManager.getDocuments()
            val items = documents.map {
                when (it) {
                    is Document.Passport ->
                        ProfileDocument(DocumentType.Passport, "${it.series} ${it.number}")
                    is Document.InternationalPassport ->
                        ProfileDocument(DocumentType.InternationalPassport, it.number)
                    is Document.HealthInsurancePolicy ->
                        ProfileDocument(DocumentType.Policy, it.number)
                    is Document.TIN ->
                        ProfileDocument(DocumentType.TIN, it.number)
                }
            }
            profileDocuments.postValue(items + listOf(ProfileAddDocument))
        }
    }

    private fun<T, U : T> MutableList<T>.addIf(ifValue: Boolean, value: U) {
        if (ifValue) {
            add(value)
        }
    }
}
