package com.nevmem.qms.fragments.join.service

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nevmem.qms.R
import com.nevmem.qms.ServiceProto
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
    }

    companion object {
        fun newInstance(): ServiceFragment = ServiceFragment()
    }
}
