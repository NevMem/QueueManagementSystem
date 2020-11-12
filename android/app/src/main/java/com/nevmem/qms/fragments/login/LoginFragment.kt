package com.nevmem.qms.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nevmem.qms.R
import com.nevmem.qms.auth.data.LoginState
import com.nevmem.qms.model.toast.ShowToastManager
import com.nevmem.qms.model.toast.Type
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val model: LoginPageViewModel by viewModel()

    private val showToastManager: ShowToastManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {
            setButtonEnabled(false)
            GlobalScope.launch {
                model.doLogin(loginField.text.toString(), passwordField.text.toString()).collect {
                    if (it !is LoginState.Processing) {
                        setButtonEnabled(true)
                    }
                    if (it is LoginState.Error) {
                        handleError(it.error)
                    } else if (it is LoginState.Success) {
                        launch(Dispatchers.Main) {
                            val action = LoginFragmentDirections.moveToHomeAfterLogin()
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    private fun handleError(error: String) {
        showToastManager.showToast(error, Type.Error)
    }

    private fun setButtonEnabled(enabled: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            loginButton.isEnabled = enabled
        }
    }
}
