package com.nevmem.qms.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.UserLoadingState
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.features.isFeatureEnabled
import com.nevmem.qms.knownfeatures.KnownFeatures
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.toast.manager.ShowToastManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileFragmentViewModel(
    private val authManager: AuthManager,
    private val showToastManager: ShowToastManager,
    featureManager: FeatureManager
) : ViewModel() {

    private val profileList = MutableLiveData<List<RVItem>>()
    internal val profile: LiveData<List<RVItem>> = profileList

    private val visitedList = MutableLiveData<List<RVItem>>()
    internal val visited: LiveData<List<RVItem>> = visitedList

    private var user: UserLoadingState.User? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            updateData()
        }

    init {
        viewModelScope.launch {
            authManager.currentUser().collect {
                if (it is UserLoadingState.Error) {
                    showToastManager.error(it.message)
                } else if (it is UserLoadingState.User) {
                    user = it
                }
            }
        }
        if (featureManager.isFeatureEnabled(KnownFeatures.ShowHistoryOnProfilePage.value)) {
            visitedList.postValue((0..30).map {
                listOf(
                    ProfileVisitedPlace(
                        "Hospital",
                        "https://gb2bel.belzdrav.ru/upload/medialibrary/1a8/%D0%B3%D0%BB%D0%B0%D0%B2.jpg",
                        listOf("Medical", "Health care")
                    ),
                    ProfileVisitedPlace(
                        "Yandex",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/d/db/Yandex_Logo.svg/1200px-Yandex_Logo.svg.png",
                        listOf("IT", "Company")
                    ),
                    ProfileVisitedPlace(
                        "Check tags",
                        "https://blackbirdesolutions.com/files/2020/02/tags-and-categories.jpg",
                        (0..10).map { "Tag $it" })
                )
            }.flatten())
        }
    }

    private fun updateData() {
        profileList.postValue(mutableListOf<RVItem>().apply {
            add(ProfileAvatar("https://pickaface.net/gallery/avatar/unr_sample_161118_2054_ynlrg.png"))
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
