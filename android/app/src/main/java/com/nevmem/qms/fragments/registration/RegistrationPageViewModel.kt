package com.nevmem.qms.fragments.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationPageViewModel : ViewModel() {
    private val errorsLiveData = MutableLiveData<String>()
    val errors: LiveData<String> = errorsLiveData

    private val nameCheck = MutableLiveData<String?>()
    val nameVerification: LiveData<String?> = nameCheck

    private val surnameCheck = MutableLiveData<String?>()
    val surnameVerification: LiveData<String?> = surnameCheck

    private val passwordCheck = MutableLiveData<String?>()
    val passwordVerification: LiveData<String?> = passwordCheck

    private val emailCheck = MutableLiveData<String?>()
    val emailVerification: LiveData<String?> = emailCheck

    fun processRegistration(name: String?, surname: String?, password: String?, email: String?) {
        checkName(name)
        checkSurname(surname)
        checkPassword(password)
        checkEmail(email)
    }

    private fun checkName(name: String?) {
        if (name?.isEmpty() != false) {
            nameCheck.postValue("Required field")
        } else {
            nameCheck.postValue(null)
        }
    }

    private fun checkSurname(surname: String?) {
        if (surname?.isEmpty() != false) {
            surnameCheck.postValue("Required field")
        } else {
            surnameCheck.postValue(null)
        }
    }

    private fun checkPassword(password: String?) {
        if (password?.isEmpty() != false) {
            passwordCheck.postValue("Required field")
        } else {
            passwordCheck.postValue(null)
        }
    }

    private fun checkEmail(email: String?) {
        if (email?.isEmpty() != false) {
            emailCheck.postValue("Required field")
        } else {
            emailCheck.postValue(null)
        }
    }
}
