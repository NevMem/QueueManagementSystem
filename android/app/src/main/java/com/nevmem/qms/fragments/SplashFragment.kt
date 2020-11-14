package com.nevmem.qms.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nevmem.qms.R
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.AuthenticationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashFragment : Fragment() {

    private val authManager: AuthManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch {
            delay(250)
            for (elem in authManager.authenticationStatus) {
                when (elem) {
                    is AuthenticationStatus.LoggedIn -> {
                        val action = SplashFragmentDirections.actionOnLoggedIn()
                        launch(Dispatchers.Main) {
                            findNavController().navigate(action)
                        }
                    }
                    is AuthenticationStatus.Unauthorized -> {
                        val action = SplashFragmentDirections.actionUnAuthorized()
                        launch(Dispatchers.Main) {
                            findNavController().navigate(action)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
