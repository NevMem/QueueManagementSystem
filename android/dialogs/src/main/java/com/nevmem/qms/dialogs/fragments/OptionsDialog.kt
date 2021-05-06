package com.nevmem.qms.dialogs.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.nevmem.qms.dialogs.DialogsManager
import com.nevmem.qms.dialogs.R
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.BaseRecyclerAdapter
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory

class OptionsDialog : BottomSheetDialogFragment() {

    lateinit var onResult: (DialogsManager.OptionItem<*>) -> Unit
    lateinit var onDismiss: () -> Unit
    lateinit var message: String
    lateinit var options: List<DialogsManager.OptionItem<*>>

    private var willBeDismissed = false

    private val cancelButton by lazy {
        requireView().findViewById<MaterialButton>(R.id.cancelButton)
    }
    private val dialogMessage by lazy {
        requireView().findViewById<AppCompatTextView>(R.id.dialogMessage)
    }
    private val optionsRecycler by lazy {
        requireView().findViewById<RecyclerView>(R.id.optionsRecycler)
    }

    data class RVOptionItem(val item: DialogsManager.OptionItem<*>) : RVItem()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_options_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        optionsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        optionsRecycler.adapter = BaseRecyclerAdapter(
            options.map { RVOptionItem(it) },
            OptionItemFactory())

        dialogMessage.text = message

        cancelButton.setOnClickListener {
            willBeDismissed = true
            onDismiss()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!willBeDismissed) {
            onDismiss()
        }
    }

    inner class OptionItemFactory : RVItemFactory {
        inner class Holder(view: View): RVHolder(view) {
            override fun onBind(item: RVItem) {
                item as RVOptionItem
                itemView.findViewById<AppCompatImageView>(R.id.optionImage)
                    .setImageDrawable(ContextCompat.getDrawable(requireContext(), item.item.iconId))
                itemView.findViewById<AppCompatTextView>(
                    R.id.optionDescription).text = item.item.name
                itemView.setOnClickListener {
                    willBeDismissed = true
                    onResult(item.item)
                    dismiss()
                }
            }
        }

        override fun isAppropriateType(item: RVItem): Boolean = item is RVOptionItem

        override fun createHolder(root: ViewGroup): RVHolder
            = Holder(requireContext().inflate(R.layout.layout_option_item, root))
    }

    companion object {
        fun newInstance(): OptionsDialog = OptionsDialog()
    }
}
