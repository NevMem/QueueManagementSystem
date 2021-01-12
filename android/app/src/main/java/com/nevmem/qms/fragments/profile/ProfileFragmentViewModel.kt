package com.nevmem.qms.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nevmem.qms.recycler.RVItem

class ProfileFragmentViewModel : ViewModel() {

    private val profileList = MutableLiveData<List<RVItem>>()
    internal val profile: LiveData<List<RVItem>> = profileList

    private val visitedList = MutableLiveData<List<RVItem>>()
    internal val visited: LiveData<List<RVItem>> = visitedList

    data class ProfileAvatar(var avatarUrl: String? = null): RVItem()
    data class ProfileName(var name: String = ""): RVItem()
    data class ProfileLastName(var lastName: String = ""): RVItem()
    data class ProfileRating(var rating: Double = 0.0): RVItem()
    data class ProfileEmail(var email: String = ""): RVItem()
    enum class DocumentType {
        Passport,
        InternationalPassport,
        Policy,
    }
    data class ProfileDocument(var type: DocumentType, var number: String? = null): RVItem()

    data class ProfileVisitedPlace(
        var title: String = "",
        var imageUrl: String? = null,
        var tags: List<String> = emptyList()
    ) : RVItem()

    init {
        profileList.postValue(listOf(
            ProfileAvatar("https://pickaface.net/gallery/avatar/unr_sample_161118_2054_ynlrg.png"),
            ProfileName("Игорь"),
            ProfileLastName("Зверев"),
            ProfileEmail("memlolkek@gmail.com"),
            ProfileRating(4.92),
            ProfileDocument(DocumentType.Passport, "9214 775590"),
            ProfileDocument(DocumentType.InternationalPassport, "9214775590"),
            ProfileDocument(DocumentType.Policy, "*******9999")
        ))

        visitedList.postValue((0..30).map { listOf(
            ProfileVisitedPlace("Hospital", "https://gb2bel.belzdrav.ru/upload/medialibrary/1a8/%D0%B3%D0%BB%D0%B0%D0%B2.jpg", listOf("Medical", "Health care")),
            ProfileVisitedPlace("Yandex", "https://upload.wikimedia.org/wikipedia/commons/thumb/d/db/Yandex_Logo.svg/1200px-Yandex_Logo.svg.png", listOf("IT", "Company")),
            ProfileVisitedPlace("Check tags", "https://blackbirdesolutions.com/files/2020/02/tags-and-categories.jpg", (0..10).map { "Tag $it" })
        ) }.flatten())
    }
}
