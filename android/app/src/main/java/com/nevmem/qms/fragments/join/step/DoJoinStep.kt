package com.nevmem.qms.fragments.join.step

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.nevmem.qms.OrganizitionProto
import com.nevmem.qms.R
import com.nevmem.qms.fragments.join.JoinStep
import com.nevmem.qms.fragments.join.JoinUsecase
import com.nevmem.qms.fragments.join.step.gallery.GalleryItem
import com.nevmem.qms.fragments.join.step.gallery.GalleryItemFactory
import com.nevmem.qms.fragments.join.step.services.ServiceItem
import com.nevmem.qms.fragments.join.step.services.ServiceItemFactory
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.BaseRecyclerAdapter
import com.nevmem.qms.status.FetchStatus
import com.nevmem.qms.status.StatusProvider
import com.nevmem.qms.toast.manager.ShowToastManager
import kotlinx.android.synthetic.main.fragment_step_do_join.*
import kotlinx.android.synthetic.main.layout_media_link.view.*
import org.koin.android.ext.android.inject

class DoJoinStep : JoinStep {
    override fun createFragment(): Fragment = DoJoinFragment()

    companion object {
        class DoJoinFragment : Fragment(R.layout.fragment_step_do_join) {

            private val usecase: JoinUsecase by inject()
            private val statusProvider: StatusProvider by inject()
            private val showToastManager: ShowToastManager by inject()

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                usecase.fetchStatus?.let { status ->
                    if (status is FetchStatus.Success) {
                        updateUi(status.payload)

                        /* joinButton.isEnabled = true
                        joinButton.setOnClickListener {
                            joinButton.isEnabled = false
                            runOnUi {
                                statusProvider.join(status.payload).collect {
                                    when (it) {
                                        is JoinStatus.Pending -> {}
                                        is JoinStatus.Success -> {
                                            joinButton.isEnabled = true
                                            findNavController().navigate(JoinFragmentDirections.moveToStatusPage())
                                        }
                                        is JoinStatus.Error -> {
                                            joinButton.isEnabled = true
                                            showToastManager.error(it.message)
                                        }
                                    }
                                }
                            }
                        } */
                    }
                }
            }

            private fun updateUi(org: OrganizitionProto.Organization) {
                inviteName.text = org.info.name
                inviteAddress.text = org.info.address
                inviteDescription.isVisible = false

                listOf(
                    ::updateDescription,
                    ::updateGallery,
                    ::updateMediaLinks
                ).forEach {
                    it(org.info.dataMap)
                }

                services.adapter = BaseRecyclerAdapter(
                    org.servicesList.map { ServiceItem(it) },
                    ServiceItemFactory(requireContext(), parentFragmentManager))
            }

            private fun updateMediaLinks(data: Map<String, String>) {
                val mediaLinks = data["media_links"]?.split(",") ?: listOf()
                socialIcons.removeAllViews()
                mediaLinks.mapNotNull {
                    val link = data["link_$it"]
                    if (link != null)
                        it to link
                    else null
                }.map {
                    val iconRes = when(it.first) {
                        "instagram" -> R.drawable.icon_instagram
                        "vk" -> R.drawable.icon_vk
                        "twitter" -> R.drawable.icon_twitter
                        else -> R.drawable.icon_social_media
                    }
                    iconRes to it.second
                }.forEach { row ->
                    val view = requireContext().inflate(R.layout.layout_media_link, socialIcons)
                    view.socialButton.setImageResource(row.first)
                    view.socialButton.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(row.second))
                        startActivity(intent)
                    }
                    socialIcons.addView(view)
                }
            }

            private fun updateDescription(data: Map<String, String>) {
                if (data.containsKey("description")) {
                    inviteDescription.isVisible = true
                    inviteDescription.text = data["description"]
                }
            }

            private fun updateGallery(data: Map<String, String>) {
                val countImages = data["image_count"]?.toIntOrNull() ?: 0
                val images = mutableListOf<String>()
                for (i in 0 until countImages) {
                    val image = data["image_$i"]
                    if (image != null) {
                        images += image
                    }
                }
                gallery.isVisible = images.isNotEmpty()
                gallery.adapter = BaseRecyclerAdapter(
                    images.map { GalleryItem(it) },
                    GalleryItemFactory(requireContext()),
                    useAnimation = true)
            }
        }
    }
}
