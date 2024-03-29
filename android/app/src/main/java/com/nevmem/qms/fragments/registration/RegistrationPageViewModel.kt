package com.nevmem.qms.fragments.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.RegisterCredentials
import com.nevmem.qms.auth.data.RegisterState
import com.nevmem.qms.fragments.registration.checks.VerificationUsecase
import com.nevmem.qms.statemachine.*
import com.nevmem.qms.toast.manager.ShowToastManager
import com.yandex.metrica.YandexMetrica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

data class Checks(val passed: MutableSet<String>) : State()
object AllChecksPassed : State()
object ProcessingRegistration : State()

data class CheckScheduled(val check: String) : Event()
data class CheckPassed(val check: String) : Event()
data class CheckFailed(val check: String) : Event()
object ProcessRegistration : Event()
object RegistrationFailed : Event()
object RegistrationSucceeded : Event()

private const val NAME_CHECK = "name"
private const val SURNAME_CHECK = "surname"
private const val EMAIL_CHECK = "email"
private const val PASSWORD_CHECK = "password"
private val allChecks = mutableSetOf(NAME_CHECK, SURNAME_CHECK, EMAIL_CHECK, PASSWORD_CHECK)

class RegistrationPageViewModel(
    private val authManager: AuthManager,
    private val showToastManager: ShowToastManager
) : ViewModel() {
    private val errorsLiveData = MutableLiveData<String>()
    val errors: LiveData<String> = errorsLiveData

    private val liveDataForCheck = mapOf(
        NAME_CHECK to MutableLiveData<String?>(),
        SURNAME_CHECK to MutableLiveData<String?>(),
        PASSWORD_CHECK to MutableLiveData<String?>(),
        EMAIL_CHECK to MutableLiveData<String?>()
    )

    val nameVerification: LiveData<String?> = liveDataForCheck.getValue(NAME_CHECK)
    val surnameVerification: LiveData<String?> = liveDataForCheck.getValue(SURNAME_CHECK)
    val passwordVerification: LiveData<String?> = liveDataForCheck.getValue(PASSWORD_CHECK)
    val emailVerification: LiveData<String?> = liveDataForCheck.getValue(EMAIL_CHECK)

    private val changingEnabled = MutableLiveData<Boolean>(true)
    val fieldsEnabled: LiveData<Boolean> = changingEnabled

    private val regEnabled = MutableLiveData<Boolean>(false)
    val registrationEnabled: LiveData<Boolean> = regEnabled

    private val machine = createSyncStateMachine(Checks(mutableSetOf()))

    private val success = MutableLiveData<Boolean>()
    val registrationSucceded: LiveData<Boolean> = success

    private val checkers = mapOf(
        NAME_CHECK to VerificationUsecase(VerificationUsecase.Type.NameSurnameCheck),
        SURNAME_CHECK to VerificationUsecase(VerificationUsecase.Type.NameSurnameCheck),
        PASSWORD_CHECK to VerificationUsecase(VerificationUsecase.Type.PasswordCheck),
        EMAIL_CHECK to VerificationUsecase(VerificationUsecase.Type.Email)
    )

    init {
        checkers.forEach { entity ->
            entity.value.listener = createVerificationUsecaseListener(entity.key)
        }

        machine.state(Checks::class.java) {
            handler<CheckScheduled> { event ->
                val passed = (currentState as Checks).passed.filter { it != event.check }.toMutableSet()
                transition(Checks(passed))
            }
            handler<CheckFailed> { event ->
                val passed = (currentState as Checks).passed.filter { it != event.check }.toMutableSet()
                transition(Checks(passed))
            }
            handler<CheckPassed> {
                val passed = mutableSetOf<String>().apply {
                    addAll((currentState as Checks).passed)
                    add(it.check)
                }
                if (passed.size == 4) {
                    transition(AllChecksPassed)
                } else {
                    transition(Checks(passed))
                }
            }
        }

        machine.state(AllChecksPassed::class.java) {
            handler<CheckScheduled> { event ->
                val passed = mutableSetOf<String>()
                passed.addAll(allChecks.filter { it != event.check })
                transition(Checks(passed))
            }
            handler<ProcessRegistration> { event ->
                transition(ProcessingRegistration)
            }
        }

        machine.state(ProcessingRegistration::class.java) {
            handler<RegistrationFailed> {
                transition(AllChecksPassed)
            }
            handler<RegistrationSucceeded> {
                success.postValue(true)
                transition(AllChecksPassed)
            }
        }

        machine.setStateDelegate { state ->
            regEnabled.postValue(state == AllChecksPassed)
            changingEnabled.postValue(state != ProcessingRegistration)
            YandexMetrica.reportEvent("reg-page-vm-state-change", mapOf(
                "new-state" to state
            ))
        }
    }

    fun nameChanged(value: String?) = fieldChanged(NAME_CHECK, value)
    fun surnameChanged(value: String?) = fieldChanged(SURNAME_CHECK, value)
    fun passwordChanged(value: String?) = fieldChanged(PASSWORD_CHECK, value)
    fun emailChanged(value: String?) = fieldChanged(EMAIL_CHECK, value)

    fun processRegistration(name: String?, surname: String?, password: String?, email: String?) {
        machine.dispatchEvent(ProcessRegistration)
        GlobalScope.launch {
            authManager.register(RegisterCredentials(email!!, password!!, name!!, surname!!)).collect { state ->
                if (state is RegisterState.Error) {
                    launch(Dispatchers.Main) { showToastManager.error(state.error) }
                    machine.dispatchEvent(RegistrationFailed)
                } else if (state == RegisterState.Success) {
                    launch(Dispatchers.Main) { showToastManager.success("Registration succeeded") }
                    machine.dispatchEvent(RegistrationSucceeded)
                }
            }
        }
    }

    private fun fieldChanged(check: String, value: String?) {
        checkers.getValue(check).update(value)
    }

    private fun showCheckError(checkName: String, message: String) {
        liveDataForCheck.getValue(checkName).postValue(message)
    }

    private fun removeCheckError(checkName: String) {
        liveDataForCheck.getValue(checkName).postValue(null)
    }

    private fun createVerificationUsecaseListener(check: String): ((VerificationUsecase.VerificationStatus) -> Unit) {
        return { status ->
            when (status) {
                VerificationUsecase.VerificationStatus.Scheduled -> {
                    machine.dispatchEvent(CheckScheduled(check))
                    removeCheckError(check)
                }
                VerificationUsecase.VerificationStatus.Success -> {
                    machine.dispatchEvent(CheckPassed(check))
                    removeCheckError(check)
                }
                is VerificationUsecase.VerificationStatus.Error -> {
                    machine.dispatchEvent(CheckFailed(check))
                    showCheckError(check, status.message)
                }
            }
        }
    }
}
