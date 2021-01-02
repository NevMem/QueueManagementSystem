package com.nevmem.qms.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nevmem.qms.recycler.RVItem

class ProfileFragmentViewModel : ViewModel() {

    private val profileList = MutableLiveData<List<RVItem>>()
    internal val profile: LiveData<List<RVItem>> = profileList

    data class ProfileAvatar(var avatarUrl: String? = null): RVItem()
    data class ProfileName(var name: String = ""): RVItem()
    data class ProfileLastName(var lastName: String = ""): RVItem()
    data class ProfileRating(var rating: Double = 0.0): RVItem()
    data class ProfileEmail(var email: String = ""): RVItem()

    init {
        profileList.postValue(listOf(
            ProfileAvatar("https://pickaface.net/gallery/avatar/unr_sample_161118_2054_ynlrg.png"),
            ProfileName("Игорь"),
            ProfileLastName("Зверев"),
            ProfileEmail("memlolkek@gmail.com"),
            ProfileRating(4.92)
        ))
    }
}
