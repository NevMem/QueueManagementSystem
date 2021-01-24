package com.nevmem.qms.fragments.registration

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nevmem.qms.R
import org.koin.android.viewmodel.ext.android.viewModel

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private val model: RegistrationPageViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
