package com.nevmem.qms.documents.utils

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.documents.Document
import com.nevmem.qms.documents.parser.DocumentsParser

val ClientApiProto.User?.parseDocuments: List<Document>
    get() {
        return DocumentsParser.parse(this?.dataMap ?: emptyMap())
    }
