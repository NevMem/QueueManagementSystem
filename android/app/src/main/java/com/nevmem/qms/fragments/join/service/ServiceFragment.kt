package com.nevmem.qms.fragments.join.service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nevmem.qms.R
import com.nevmem.qms.ServiceProto
import com.nevmem.qms.recycler.BaseRecyclerAdapter
import com.nevmem.qms.recycler.RVItem
import kotlinx.android.synthetic.main.fragment_service.*

class ServiceFragment : BottomSheetDialogFragment() {

    private var service: ServiceProto.Service? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        service?.let { updateUi(it) }
    }

    fun setService(service: ServiceProto.Service) {
        this.service = service
    }

    private fun updateUi(service: ServiceProto.Service) {
        serviceName.text = service.info.name

        updateRecyclerItems(service.info.dataMap)
    }

    private fun updateRecyclerItems(data: Map<String, String>) {
        val listItems = mutableListOf<RVItem>().apply {
            if (data.containsKey("description")) {
                val description = data.getValue("description")
                add(DescriptionItem(description))
            }

            val items = (data["checklistItems"] ?: "").split(",").filter { it.isNotEmpty() }
            if (items.isNotEmpty()) {
                add(ChecklistHeader)
                repeat(4) { items.forEach {
                    add(ChecklistItem(it))
                }}
            }
        }

        recycler.adapter = BaseRecyclerAdapter(
            listItems,
            DescriptionItemFactory(requireContext()),
            ChecklistHeaderFactory(requireContext()),
            ChecklistItemFactory(requireContext()))
    }

    companion object {
        fun newInstance(): ServiceFragment = ServiceFragment()
    }
}
