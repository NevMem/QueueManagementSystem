package com.nevmem.qms.fragments.profile

import com.nevmem.qms.recycler.RVItem


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
object ProfileAddDocument : RVItem()

data class ProfileVisitedPlace(
        var title: String = "",
        var imageUrl: String? = null,
        var tags: List<String> = emptyList()
) : RVItem()

object HeaderStub : RVItem()

object SpaceStub : RVItem()
