package com.nevmem.qms.fragments.dev_settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.knownfeatures.KnownFeatures
import com.nevmem.qms.recycler.RVItem

class DeveloperSettingsFragmentViewModel(
    private val featureManager: FeatureManager
) : ViewModel(), FeatureManager.Listener {
    private val features = MutableLiveData<List<RVItem>>()
    val items: LiveData<List<RVItem>> = features

    data class Feature(val name: String, val textDescriptionStringId: Int) : RVItem()

    init {
        featureManager.addListener(this)
        onFeaturesUpdated()
    }

    override fun onFeaturesUpdated() {
        features.postValue(mutableListOf<RVItem>().apply {
            KnownFeatures.values().forEach {
                add(Feature(it.value, it.textDescriptionResource))
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        featureManager.removeListener(this)
    }
}
