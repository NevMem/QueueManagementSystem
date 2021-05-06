package com.nevmem.qms.documents.internal

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.documents.Document
import com.nevmem.qms.documents.DocumentsManager
import com.nevmem.qms.documents.utils.parseDocuments
import com.nevmem.qms.documents.utils.saveDocuments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

internal class DocumentsManagerImpl(
    private val authManager: AuthManager
) : DocumentsManager {

    private val mDocuments = BroadcastChannel<List<Document>>(1)

    override val documents: Flow<List<Document>>
        get() = mDocuments.openSubscription().receiveAsFlow()

    override suspend fun getDocuments(): List<Document> = suspendCoroutine { continuation ->
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val user = authManager.user()
                val result = user.parseDocuments
                mDocuments.send(result)
                continuation.resumeWith(Result.success(result))
            } catch (ex: Exception) {
                continuation.resumeWith(Result.failure(ex))
            }
        }
    }

    override suspend fun addDocument(document: Document) = suspendCoroutine<Unit> { continuation ->
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val user = authManager.user() ?: throw IllegalArgumentException()
                val documents = getDocuments().toMutableList()
                documents.find { it.javaClass == document.javaClass }?.also {
                    documents.remove(it)
                }
                documents.add(document)
                val newUser = user.saveDocuments(documents)
                authManager.updateUser(newUser)
                getDocuments()
                continuation.resumeWith(Result.success(Unit))
            } catch (ex: Exception) {
                continuation.resumeWith(Result.failure(ex))
            }
        }
    }

    override suspend fun removeDocument(document: Document) {
        TODO("Not yet implemented")
    }
}
