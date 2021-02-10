package com.nevmem.qms.fragments.dev_settings.items

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.nevmem.qms.R
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.features.isFeatureEnabled
import com.nevmem.qms.fragments.dev_settings.DeveloperSettingsFragmentViewModel
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
import kotlinx.android.synthetic.main.dev_settings_feature.view.*

class FeatureItemsFactory(
    private val context: Context,
    private val featureManager: FeatureManager
): RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as DeveloperSettingsFragmentViewModel.Feature
            itemView.featureName.text = context.resources.getText(item.textDescriptionStringId)
            itemView.featureSwitch.isChecked = featureManager.isFeatureEnabled(item.name)
            itemView.featureSwitch.setOnCheckedChangeListener { _, newValue ->
                featureManager.overrideFeature(item.name, if (newValue) "enabled" else "disabled")
            }
        }
    }


    override fun isAppropriateType(item: RVItem): Boolean = item is DeveloperSettingsFragmentViewModel.Feature
    override fun createHolder(root: ViewGroup): RVHolder = Holder(context.inflate(R.layout.dev_settings_feature, root))
}
