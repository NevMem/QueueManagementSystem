package com.nevmem.qms.feedback.data

import javax.persistence.*

@Entity
@Table(name = "feedbacks")
class RepoFeedback(
    @Column(name = "author_email") var authorEmail: String = "",
    @Column(name = "entityId") var entityId: String = "",
    @Column(name = "feedback_text", columnDefinition = "TEXT") var text: String = "",
    @Column(name = "rating") var rating: Double = -1.0
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}
