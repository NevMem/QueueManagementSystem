package com.nevmem.qms.fragments.registration.checks

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VerificationUsecase(private val type: Type) {

    enum class Type {
        NameSurnameCheck,
        PasswordCheck,
        Email
    }

    sealed class VerificationStatus {
        object Scheduled: VerificationStatus()
        object Success: VerificationStatus()
        data class Error(var message: String): VerificationStatus()
    }

    private var job: Job? = null

    var listener: ((VerificationStatus) -> Unit)? = null

    fun update(value: String?) {
        job?.cancel()
        listener?.invoke(VerificationStatus.Scheduled)
        job = GlobalScope.launch {
            for (check in assembleCheckList(type)) {
                val result = check.check(value)
                if (result is Check.Result.Error) {
                    launch(Dispatchers.Main) {
                        listener?.invoke(VerificationStatus.Error(result.message))
                    }
                    return@launch
                }
            }
            launch(Dispatchers.Main) {
                listener?.invoke(VerificationStatus.Success)
            }
        }
    }

    private fun assembleCheckList(type: Type): List<Check> = mutableListOf<Check>().apply {
        add(EmptinessCheck())
        if (type == Type.Email) {
            add(EmailCheck())
        }
        if (type == Type.PasswordCheck) {
            add(TooShortCheck())
            add(SimplePasswordCheck())
        }
    }
}
