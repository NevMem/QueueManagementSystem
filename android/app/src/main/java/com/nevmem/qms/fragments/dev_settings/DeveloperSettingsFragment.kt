package com.nevmem.qms.fragments.dev_settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.qms.R
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.fragments.dev_settings.items.FeatureItemsFactory
import com.nevmem.qms.recycler.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_developer_settings.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class DeveloperSettingsFragment : Fragment(R.layout.fragment_developer_settings) {

    private val model: DeveloperSettingsFragmentViewModel by viewModel()
    private val featureManager: FeatureManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.items.observe(viewLifecycleOwner, Observer { list ->
            recycler.adapter = BaseRecyclerAdapter(
                list,
                FeatureItemsFactory(requireContext(), featureManager),
                useAnimation = true)
        })
    }
}
