package com.nevmem.qms.documents

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.documents.internal.DocumentsManagerImpl

fun createDocumentsManager(
    authManager: AuthManager
): DocumentsManager = DocumentsManagerImpl(authManager)
