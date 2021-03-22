package com.nevmem.qms.fragments.join.step.services

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.nevmem.qms.R
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.features.isFeatureEnabled
import com.nevmem.qms.fragments.join.service.ServiceFragment
import com.nevmem.qms.inflate
import com.nevmem.qms.knownfeatures.KnownFeatures
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
import kotlinx.android.synthetic.main.layout_service_item.view.*

class ServiceItemFactory(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val featureManager: FeatureManager
) : RVItemFactory {
    inner class ServiceItemHolder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as ServiceItem
            itemView.serviceName.text = item.service.info.name
            itemView.ratingView.isVisible = featureManager.isFeatureEnabled(KnownFeatures.RatingsForServices.value)
            itemView.ratingView.setRatingId("service_${item.service.info.id}")

            itemView.setOnClickListener {
                val fragment = ServiceFragment.newInstance()
                fragment.setService(item.service)
                fragment.show(fragmentManager, "service")
            }
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is ServiceItem
    override fun createHolder(root: ViewGroup): RVHolder
        = ServiceItemHolder(context.inflate(R.layout.layout_service_item, root))
}
