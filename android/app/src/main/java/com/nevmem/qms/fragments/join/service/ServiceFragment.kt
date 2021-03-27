package com.nevmem.qms.fragments.join.service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nevmem.qms.R
import com.nevmem.qms.ServiceProto
import com.nevmem.qms.common.utils.runOnUi
import com.nevmem.qms.data.feedback.Feedback
import com.nevmem.qms.feedback.FeedbackManager
import com.nevmem.qms.recycler.BaseRecyclerAdapter
import com.nevmem.qms.recycler.RVItem
import kotlinx.android.synthetic.main.fragment_service.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class ServiceFragment : BottomSheetDialogFragment() {

    private var service: ServiceProto.Service? = null

    private var job: Job? = null

    sealed class FeedbackState {
        object None: FeedbackState()
        object Loading: FeedbackState()
        data class Loaded(val feedback: List<Feedback>): FeedbackState()
        data class Error(val error: String): FeedbackState()
    }
    private var feedback: FeedbackState = FeedbackState.None
        set(value) {
            if (field == value) {
                return
            }
            field = value
            updateFeedback()
        }

    private val feedbackManager: FeedbackManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        service?.let {
            updateUi(it)

            job?.cancel()
            feedback = FeedbackState.Loading
            job = GlobalScope.launch(Dispatchers.IO) {
                try {
                    val data = feedbackManager.loadFeedback("service_${it.info.id}")
                    runOnUi { feedback = FeedbackState.Loaded(data) }
                } catch (exception: Exception) {
                    runOnUi { feedback = FeedbackState.Error(exception.message ?: "") }
                }
            }
        }
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
                items.forEach {
                    add(ChecklistItem(it))
                }
            }
        }

        recycler.adapter = BaseRecyclerAdapter(
            listItems,
            DescriptionItemFactory(requireContext()),
            ChecklistHeaderFactory(requireContext()),
            ChecklistItemFactory(requireContext()),
            NoFeedbackItemFactory(requireContext()))
    }

    private fun updateFeedback() {
        val items = mutableListOf<RVItem>().apply {
            when (val feedback = feedback) {
                FeedbackState.None -> {}
                FeedbackState.Loading -> {
                    add(LoadingFeedbackItem)
                }
                is FeedbackState.Error -> {
                    add(ErrorFeedbackItem(feedback.error))
                }
                is FeedbackState.Loaded -> {
                    feedback.feedback.forEach {
                        add(FeedbackItem(it))
                    }
                    if (feedback.feedback.isEmpty()) {
                        add(NoFeedbackItem)
                    }
                }
            }
        }

        feedbackRecycler.adapter = BaseRecyclerAdapter(
            items,
            LoadingFeedbackItemFactory(requireContext()),
            ErrorFeedbackItemFactory(requireContext()),
            FeedbackItemFactory(requireContext()),
            NoFeedbackItemFactory(requireContext()))
    }

    companion object {
        fun newInstance(): ServiceFragment = ServiceFragment()
    }
}
