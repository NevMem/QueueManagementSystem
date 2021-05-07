package com.nevmem.qms.fragments.profile

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.nevmem.qms.R
import com.nevmem.qms.documents.usecase.AddDocumentUsecaseFactory
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
import kotlinx.android.synthetic.main.profile_document.view.*

internal class ProfileDocumentFactory(private val context: Context) : RVItemFactory {

    fun DocumentType.toResourceId(): Int {
        return when (this) {
            DocumentType.Passport -> R.drawable.icon_passport
            DocumentType.InternationalPassport -> R.drawable.icon_international_passport
            DocumentType.Policy -> R.drawable.icon_insurance
            DocumentType.TIN -> R.drawable.icon_insurance
        }
    }

    fun DocumentType.toNameTextId(): Int {
        return when (this) {
            DocumentType.Passport -> R.string.profile_passport
            DocumentType.InternationalPassport -> R.string.profile_international_passport
            DocumentType.Policy -> R.string.profile_health_insurance
            DocumentType.TIN -> R.string.profile_tin
        }
    }

    private inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as ProfileDocument
            itemView.apply {
                documentIcon.setImageResource(item.type.toResourceId())
                documentName.text = context.resources.getText(item.type.toNameTextId())
                documentNumber.text = "${context.resources.getText(R.string.profile_document_number)} ${item.number}"
            }
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is ProfileDocument
    override fun createHolder(root: ViewGroup): RVHolder = Holder(context.inflate(R.layout.profile_document, root))
}

internal class ProfileAddDocumentFactory(
    private val context: Context,
    private val addDocumentUsecaseFactory: AddDocumentUsecaseFactory
) : RVItemFactory {
    private inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            itemView.setOnClickListener {
                addDocumentUsecaseFactory.createUsecase().launchUsecase()
            }
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is ProfileAddDocument
    override fun createHolder(root: ViewGroup): RVHolder =
        Holder(context.inflate(R.layout.profile_add_document, root))
}
