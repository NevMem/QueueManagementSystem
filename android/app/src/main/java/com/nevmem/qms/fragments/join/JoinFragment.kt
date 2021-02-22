package com.nevmem.qms.fragments.join

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nevmem.qms.R
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.features.isFeatureEnabled
import com.nevmem.qms.fragments.join.step.DoJoinStep
import com.nevmem.qms.fragments.join.step.InviteStep
import com.nevmem.qms.fragments.join.transformers.ZoomOutPageTransformer
import com.nevmem.qms.knownfeatures.KnownFeatures
import com.nevmem.qms.status.FetchStatus
import kotlinx.android.synthetic.main.fragment_join.*
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module

class JoinFragment : Fragment(R.layout.fragment_join), JoinUsecase {
    private val steps: List<JoinStep> = listOf(InviteStep(), DoJoinStep())

    private val featureManager: FeatureManager by inject()

    private val useAnimations: Boolean by lazy {
        featureManager.isFeatureEnabled(KnownFeatures.UseAnimationsOnJoinPage.value)
    }

    private val joinModules = module {
        single<JoinUsecase> { this@JoinFragment }
    }

    override var fetchStatus: FetchStatus? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            if (field is FetchStatus.Success) {
                moveToSecondStep()
            }
        }

    override val invite = MutableLiveData<String>()

    override fun onResume() {
        super.onResume()
        loadKoinModules(joinModules)
    }

    override fun onPause() {
        super.onPause()
        unloadKoinModules(joinModules)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.adapter = JoinPagerAdapter(this, steps)
        pager.isUserInputEnabled = false
        if (useAnimations) {
            pager.setPageTransformer(ZoomOutPageTransformer())
        }

        arguments?.getString("invite_id")?.let { invite_id ->
            invite.postValue(invite_id)
        }
    }

    private fun moveToSecondStep() {
        if (useAnimations) {
            pager.beginFakeDrag()
            val width = -requireView().width.toFloat()
            ValueAnimator.ofFloat(0f, 1f).apply {
                var prevValue = 0f
                addUpdateListener { animator ->
                    pager.fakeDragBy(-width * (prevValue - (animator.animatedValue as Float)))
                    prevValue = animator.animatedValue as Float
                }
                addListener(onEnd = {
                    pager.endFakeDrag()
                })
                duration = 500L
                start()
            }
        } else {
            pager.currentItem = 1
        }
    }

    private inner class JoinPagerAdapter(fragment: Fragment, private val steps: List<JoinStep>) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = steps.size

        override fun createFragment(position: Int): Fragment = steps[position].createFragment()
    }
}
