package com.nevmem.qms.fragments.join.step

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.nevmem.qms.R
import com.nevmem.qms.fragments.join.JoinStep
import com.nevmem.qms.fragments.join.JoinUsecase
import com.nevmem.qms.status.FetchStatus
import com.nevmem.qms.status.StatusProvider
import com.nevmem.qms.toast.manager.ShowToastManager
import com.nevmem.qms.toast.manager.Type
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.fragment_step_invite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class InviteStep : JoinStep {
    override fun createFragment(): Fragment = InviteFragment()

    companion object {
        class InviteFragment : Fragment(R.layout.fragment_step_invite) {

            private val usecase: JoinUsecase by inject()
            private val statusProvider: StatusProvider by inject()
            private val showToastManager: ShowToastManager by inject()

            private var animator: ValueAnimator? = null

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                usecase.invite.observe(viewLifecycleOwner, Observer {
                    inviteField.setText(it)
                })

                joinButton.setOnClickListener {
                    joinButton.isEnabled = false
                    inviteField.isEnabled = false

                    animator?.cancel()
                    animator = ValueAnimator.ofFloat(1f, 0.5f, 1f).apply {
                        duration = 1200
                        repeatCount = ValueAnimator.INFINITE
                        addUpdateListener {
                            joinIcon.alpha = it.animatedValue as Float
                        }
                        start()
                    }

                    GlobalScope.launch(Dispatchers.Main) {
                        val invite = inviteField.text?.toString() ?: ""
                        YandexMetrica.reportEvent("fetching_info_by_invite", mapOf("invite" to invite))
                        statusProvider.fetchDataForInvite(invite.toLowerCase())
                            .collect {
                                if (it is FetchStatus.Error) {
                                    showToastManager.showToast(it.message, Type.Error)
                                } else if (it is FetchStatus.Success) {
                                    showToastManager.showToast("Got invite info", Type.Success)
                                }
                                if (it !is FetchStatus.Pending) {
                                    joinButton.isEnabled = true
                                    inviteField.isEnabled = true
                                    animator?.end()
                                }
                                usecase.fetchStatus = it
                            }
                    }
                }
            }
        }
    }
}
