package com.nevmem.qms.fragments.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nevmem.qms.R
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.BaseRecyclerAdapter
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
import com.nevmem.qms.utils.ifDebug
import com.nevmem.qms.utils.livedata.mergeLatest
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_avatar.view.*
import kotlinx.android.synthetic.main.profile_email.view.*
import kotlinx.android.synthetic.main.profile_header.view.*
import kotlinx.android.synthetic.main.profile_lastname.view.*
import kotlinx.android.synthetic.main.profile_name.view.*
import kotlinx.android.synthetic.main.profile_rating.view.*
import kotlinx.android.synthetic.main.profile_visited.view.*
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
                ProfileRatingFactory(),
                ProfileVisitedFactory(),
                HeaderFactory())
        })
    }

    private class ProfileAvatarFactory(private val context: Context) : RVItemFactory {
        private inner class Holder(view: View) : RVHolder(view) {
            override fun onBind(item: RVItem) {
                item as ProfileFragmentViewModel.ProfileAvatar
                Glide.with(context)
                    .load(item.avatarUrl)
                    .placeholder(R.drawable.icon_profile)
                    .apply(RequestOptions().circleCrop())
                    .into(itemView.avatarImage)
            }
        }
        override fun isAppropriateType(item: RVItem): Boolean = item is ProfileFragmentViewModel.ProfileAvatar
        override fun createHolder(root: ViewGroup): RVHolder
            = Holder(LayoutInflater.from(context).inflate(R.layout.profile_avatar, root, false))
    }

    private class ProfileNameFactory(private val context: Context) : RVItemFactory {
        private inner class Holder(view: View) : RVHolder(view) {
            override fun onBind(item: RVItem) {
                item as ProfileFragmentViewModel.ProfileName
                itemView.profileName.text = item.name
            }
        }
        override fun isAppropriateType(item: RVItem): Boolean = item is ProfileFragmentViewModel.ProfileName
        override fun createHolder(root: ViewGroup): RVHolder
            = Holder(LayoutInflater.from(context).inflate(R.layout.profile_name, root, false))
    }

    private class ProfileLastNameFactory(private val context: Context) : RVItemFactory {
        private inner class Holder(view: View) : RVHolder(view) {
            override fun onBind(item: RVItem) {
                item as ProfileFragmentViewModel.ProfileLastName
                itemView.profileLastName.text = item.lastName
            }
        }
        override fun isAppropriateType(item: RVItem): Boolean = item is ProfileFragmentViewModel.ProfileLastName
        override fun createHolder(root: ViewGroup): RVHolder
            = Holder(LayoutInflater.from(context).inflate(R.layout.profile_lastname, root, false))
    }

    private class ProfileEmailFactory(private val context: Context) : RVItemFactory {
        private inner class Holder(view: View) : RVHolder(view) {
            override fun onBind(item: RVItem) {
                item as ProfileFragmentViewModel.ProfileEmail
                itemView.profileEmail.text = item.email
            }
        }
        override fun isAppropriateType(item: RVItem): Boolean = item is ProfileFragmentViewModel.ProfileEmail
        override fun createHolder(root: ViewGroup): RVHolder
            = Holder(LayoutInflater.from(context).inflate(R.layout.profile_email, root, false))
    }

    private inner class ProfileRatingFactory : RVItemFactory {
        private inner class Holder(view: View) : RVHolder(view) {
            override fun onBind(item: RVItem) {
                item as ProfileFragmentViewModel.ProfileRating
                itemView.profileRating.text = "${item.rating}"
                if (item.rating >= 4) {
                    itemView.profileRating.setTextColor(requireContext().getColor(R.color.successColor))
                } else {
                    itemView.profileRating.setTextColor(requireContext().getColor(R.color.errorColor))
                }
            }
        }
        override fun isAppropriateType(item: RVItem): Boolean = item is ProfileFragmentViewModel.ProfileRating
        override fun createHolder(root: ViewGroup): RVHolder
            = Holder(LayoutInflater.from(context).inflate(R.layout.profile_rating, root, false))
    }

    private inner class ProfileVisitedFactory : RVItemFactory {
        private inner class Holder(view: View) : RVHolder(view), FeatureManager.Listener {
            override fun onFeaturesUpdated() {
                itemView.tagsBox.isVisible =
                    featureManager.getFeature("visited_tags_visible") == "visible"
            }

            override fun onBind(item: RVItem) {
                item as ProfileFragmentViewModel.ProfileVisitedPlace
                Glide.with(context!!)
                    .load(item.imageUrl)
                    .into(itemView.placeIcon)
                itemView.visitedTitle.text = item.title
                itemView.tagsBox.setTags(item.tags)

                itemView.doOnAttach {
                    featureManager.addListener(this)
                    onFeaturesUpdated()

                    itemView.doOnDetach {
                        featureManager.removeListener(this)
                    }
                }
            }
        }
        override fun isAppropriateType(item: RVItem): Boolean = item is ProfileFragmentViewModel.ProfileVisitedPlace
        override fun createHolder(root: ViewGroup): RVHolder = Holder(context!!.inflate(R.layout.profile_visited, root))
    }

    private inner class HeaderFactory : RVItemFactory {
        private inner class Holder(view: View) : RVHolder(view) {
            override fun onBind(item: RVItem) {
                ifDebug {
                    itemView.settingsButton.isVisible = true
                }
            }
        }

        override fun isAppropriateType(item: RVItem): Boolean = item is HeaderStub
        override fun createHolder(root: ViewGroup): RVHolder = Holder(context!!.inflate(R.layout.profile_header, root))
    }
}
