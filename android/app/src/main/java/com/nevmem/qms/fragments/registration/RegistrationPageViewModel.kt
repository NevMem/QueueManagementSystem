package com.nevmem.qms.fragments.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationPageViewModel : ViewModel() {
    private val errorsLiveData = MutableLiveData<String>()
    val errors: LiveData<String> = errorsLiveData
}
