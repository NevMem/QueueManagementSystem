package com.nevmem.qms.documents.usecase

import com.nevmem.qms.dialogs.DialogsManager
import com.nevmem.qms.documents.DocumentsManager

class AddDocumentUsecaseFactory(
    private val documentsManager: DocumentsManager,
    private val dialogsManager: DialogsManager
) {
    fun createUsecase() = AddDocumentUsecase(documentsManager, dialogsManager)
}
