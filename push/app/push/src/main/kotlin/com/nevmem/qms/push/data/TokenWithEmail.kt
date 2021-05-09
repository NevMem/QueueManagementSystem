package com.nevmem.qms.push.data

import javax.persistence.*

@Entity
@Table(name = "tokens")
class TokenWithEmail(
    @Column(name = "email") var email: String = "",
    @Column(name = "token") var token: String = ""
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}
