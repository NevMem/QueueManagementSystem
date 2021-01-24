package com.nevmem.qms.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.nevmem.qms.R
import com.nevmem.qms.auth.data.LoginState
import com.nevmem.qms.toast.manager.ShowToastManager
import com.nevmem.qms.toast.manager.Type
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val model: LoginPageViewModel by viewModel()

    private val showToastManager: ShowToastManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.loginButtonInteractive.observe(viewLifecycleOwner, Observer {
            loginButton.isEnabled = it
        })

        model.loginErrors.observe(viewLifecycleOwner, Observer {
            showToastManager.showToast(it, Type.Error)
        })

        model.loginSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                val action = LoginFragmentDirections.moveToHomeAfterLogin()
                findNavController().navigate(action)
            }
        })

        loginButton.setOnClickListener {
            model.performLogin(loginField.text.toString(), passwordField.text.toString())
        }

        registerButton.setOnClickListener {

        }
    }
}
