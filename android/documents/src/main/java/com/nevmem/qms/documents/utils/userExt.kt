package com.nevmem.qms.documents.utils

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.documents.Document
import com.nevmem.qms.documents.utils.inserter.DocumentInserter
import com.nevmem.qms.documents.utils.parser.DocumentsParser

val ClientApiProto.User?.parseDocuments: List<Document>
    get() {
        return DocumentsParser.parse(this?.dataMap ?: emptyMap())
    }

fun ClientApiProto.User.saveDocuments(documents: List<Document>): ClientApiProto.User {
    val newUserBuilder = ClientApiProto.User.newBuilder(this)
    DocumentInserter.insertDocuments(newUserBuilder, documents)
    return newUserBuilder.build()
}
