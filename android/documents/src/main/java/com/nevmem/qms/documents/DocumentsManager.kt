package com.nevmem.qms.documents

import kotlinx.coroutines.flow.Flow

interface DocumentsManager {
    val documents: Flow<List<Document>>

    suspend fun getDocuments(): List<Document>
    suspend fun removeDocument(document: Document)
    suspend fun addDocument(document: Document)
}
