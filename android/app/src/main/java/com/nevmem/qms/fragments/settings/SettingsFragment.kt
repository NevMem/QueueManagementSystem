package com.nevmem.qms.fragments.settings

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nevmem.qms.BuildConfig
import com.nevmem.qms.R
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.BaseRecyclerAdapter
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.settings_about.view.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = BaseRecyclerAdapter(
            createContentList(),
            AboutFactory())
    }

    object AboutItem : RVItem()

    private fun createContentList(): List<RVItem> {
        return mutableListOf<RVItem>().apply {
            add(AboutItem)
        }
    }

    private inner class AboutFactory : RVItemFactory {
        private inner class Holder(view: View) : RVHolder(view) {
            override fun onBind(item: RVItem) {
                itemView.appNameText.text = resources.getText(R.string.app_name)
                itemView.appVersionText.text = "${resources.getText(R.string.version)} ${BuildConfig.VERSION_NAME}"
                itemView.setOnLongClickListener {
                    findNavController().navigate(SettingsFragmentDirections.goToDeveloperSettings())
                    true
                }
            }
        }

        override fun isAppropriateType(item: RVItem): Boolean = item is AboutItem
        override fun createHolder(root: ViewGroup): RVHolder = Holder(requireContext().inflate(R.layout.settings_about, root))
    }
}
