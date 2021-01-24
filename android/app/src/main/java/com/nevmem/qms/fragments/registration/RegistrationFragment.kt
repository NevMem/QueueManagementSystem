package com.nevmem.qms.fragments.registration

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.qms.R
import com.nevmem.qms.toast.manager.ShowToastManager
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private val model: RegistrationPageViewModel by viewModel()
    private val toastManager: ShowToastManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.errors.observe(viewLifecycleOwner, Observer {
            toastManager.error(it)
        })
    }
}
