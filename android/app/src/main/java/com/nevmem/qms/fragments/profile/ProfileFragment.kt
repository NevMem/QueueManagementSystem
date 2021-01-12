package com.nevmem.qms.fragments.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nevmem.qms.R
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.recycler.BaseRecyclerAdapter
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.utils.livedata.mergeLatest
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val model: ProfileFragmentViewModel by viewModel()

    private val featureManager: FeatureManager by inject()

    object HeaderStub : RVItem()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.layoutManager = LinearLayoutManager(context)

        mergeLatest(model.profile, model.visited).observe(viewLifecycleOwner, Observer { list ->
            recycler.adapter = BaseRecyclerAdapter(
                listOf(HeaderStub) + list,
                ProfileAvatarFactory(requireContext()),
                ProfileNameFactory(requireContext()),
                ProfileLastNameFactory(requireContext()),
                ProfileEmailFactory(requireContext()),
                ProfileRatingFactory(requireContext()),
                ProfileVisitedFactory(requireContext(), featureManager),
                ProfileDocumentFactory(requireContext()),
                HeaderFactory(findNavController(), requireContext()))
        })
    }
}