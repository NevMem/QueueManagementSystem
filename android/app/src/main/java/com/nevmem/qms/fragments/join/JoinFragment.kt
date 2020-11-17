package com.nevmem.qms.fragments.join

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nevmem.qms.R
import com.nevmem.qms.fragments.join.step.InviteStep
import kotlinx.android.synthetic.main.fragment_join.*

class JoinFragment : Fragment(R.layout.fragment_join) {
    private val steps: List<JoinStep> = listOf(InviteStep())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.adapter = JoinPagerAdapter(this, steps)
    }

    private inner class JoinPagerAdapter(fragment: Fragment, private val steps: List<JoinStep>) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = steps.size

        override fun createFragment(position: Int): Fragment = steps[position].createFragment()
    }
}
