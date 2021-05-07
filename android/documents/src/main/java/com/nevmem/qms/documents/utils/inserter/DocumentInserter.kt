package com.nevmem.qms.documents.utils.inserter

import com.google.gson.Gson
import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.documents.*
import com.nevmem.qms.documents.healthInsurancePolicyName
import com.nevmem.qms.documents.internationalPassportName
import com.nevmem.qms.documents.passportName
import com.nevmem.qms.documents.tinName

object DocumentInserter {

    private val gson = Gson()

    fun insertDocuments(builder: ClientApiProto.User.Builder, documents: List<Document>) {
        listOf(passportName, internationalPassportName, tinName, healthInsurancePolicyName)
            .forEach { builder.removeData(it) }
        documents.forEach {
            builder.putData(it.name, gson.toJson(it))
        }
    }
}
