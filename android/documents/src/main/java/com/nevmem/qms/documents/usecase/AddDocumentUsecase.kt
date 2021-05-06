package com.nevmem.qms.documents.usecase

import com.nevmem.qms.dialogs.DialogsManager
import com.nevmem.qms.documents.DocumentType
import com.nevmem.qms.documents.DocumentsManager
import com.nevmem.qms.documents.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddDocumentUsecase(
    private val documentsManager: DocumentsManager,
    private val dialogsManager: DialogsManager
) {
    fun launchUsecase() {
        GlobalScope.launch {
            val items = listOf(
                DialogsManager.OptionItem(
                    R.drawable.icon_insurance, "Insurance", DocumentType.HealthInsurancePolicy),
                DialogsManager.OptionItem(
                    R.drawable.icon_passport, "Passport", DocumentType.Passport),
                DialogsManager.OptionItem(
                    R.drawable.icon_international_passport, "Insurance", DocumentType.InternationalPassport),
                DialogsManager.OptionItem(
                    R.drawable.icon_insurance, "TIN", DocumentType.TIN)
            )
            val result = dialogsManager.showOptions("Choose document type", items)
            if (result is DialogsManager.OptionsResolution.Result) {
                val number = dialogsManager.showTextInputDialog("Enter document number", "Number")
            }
        }
    }
}
