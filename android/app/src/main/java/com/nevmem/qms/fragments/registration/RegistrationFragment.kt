package com.nevmem.qms.fragments.registration

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.nevmem.qms.R
import com.nevmem.qms.toast.manager.ShowToastManager
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.fragment_registration.passwordField
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

        model.nameVerification.observe(viewLifecycleOwner, Observer {
            nameField.error = it
        })

        model.surnameVerification.observe(viewLifecycleOwner, Observer {
            surnameField.error = it
        })

        model.emailVerification.observe(viewLifecycleOwner, Observer {
            emailField.error = it
        })

        model.passwordVerification.observe(viewLifecycleOwner, Observer {
            passwordField.error = it
        })

        model.registrationEnabled.observe(viewLifecycleOwner, Observer {
            performRegistrationButton.isEnabled = it
        })

        model.registrationSucceded.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigate(RegistrationFragmentDirections.moveToJoinAfterSuccessRegistration())
            }
        })

        val fields = listOf(
            nameField to model::nameChanged,
            surnameField to model::surnameChanged,
            passwordField to model::passwordChanged,
            emailField to model::emailChanged
        )

        fields.forEach {
            it.first.addTextChangedListener { text ->
                it.second(text?.toString())
            }
        }

        model.fieldsEnabled.observe(viewLifecycleOwner, Observer {
            fields.forEach { pair ->
                pair.first.isEnabled = it
            }
        })

        performRegistrationButton.setOnClickListener {
            model.processRegistration(
                nameField.text?.toString(), surnameField.text?.toString(), passwordField.text?.toString(), emailField.text?.toString())
        }
    }
}
