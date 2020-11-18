package com.nevmem.qms.fragments.join

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nevmem.qms.R
import com.nevmem.qms.fragments.join.step.DoJoinStep
import com.nevmem.qms.fragments.join.step.InviteStep
import com.nevmem.qms.status.FetchStatus
import kotlinx.android.synthetic.main.fragment_join.*
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module

class JoinFragment : Fragment(R.layout.fragment_join), JoinUsecase {
    private val steps: List<JoinStep> = listOf(InviteStep(), DoJoinStep())

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
    }

    private fun moveToSecondStep() {
        pager.currentItem = 1
    }

    private inner class JoinPagerAdapter(fragment: Fragment, private val steps: List<JoinStep>) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = steps.size

        override fun createFragment(position: Int): Fragment = steps[position].createFragment()
    }
}
