package com.nevmem.qms.documents.usecase

import com.nevmem.qms.dialogs.DialogsManager
import com.nevmem.qms.documents.Document
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
                if (result.value != DocumentType.Passport) {
                    val number =
                        dialogsManager.showTextInputDialog("Enter document number", "Number")
                    if (number is DialogsManager.TextInputDialogResolution.Text) {
                        val document = when (result.value) {
                            DocumentType.InternationalPassport -> Document.InternationalPassport(number.text)
                            DocumentType.TIN -> Document.TIN(number.text)
                            DocumentType.HealthInsurancePolicy -> Document.HealthInsurancePolicy(number.text)
                            else -> throw IllegalStateException()
                        }
                        try {
                            documentsManager.addDocument(document)
                            dialogsManager.showSimpleDialog("Document added")
                        } catch (ex: Exception) {
                            dialogsManager.showSimpleDialog(ex.message ?: "")
                        }
                    }
                } else {
                    val series = dialogsManager.showTextInputDialog("Enter passport series", "Series")
                    if (series is DialogsManager.TextInputDialogResolution.Dismissed) {
                        return@launch
                    }
                    val number =
                        dialogsManager.showTextInputDialog("Enter passport number", "Number")
                    if (number is DialogsManager.TextInputDialogResolution.Dismissed) {
                        return@launch
                    }
                    series as DialogsManager.TextInputDialogResolution.Text
                    number as DialogsManager.TextInputDialogResolution.Text
                    val passport = Document.Passport(series.text, number.text)
                    try {
                        documentsManager.addDocument(passport)
                        dialogsManager.showSimpleDialog("Document added")
                    } catch(ex: Exception) {
                        dialogsManager.showSimpleDialog(ex.message ?: "")
                    }
                }
            }
        }
    }
}
