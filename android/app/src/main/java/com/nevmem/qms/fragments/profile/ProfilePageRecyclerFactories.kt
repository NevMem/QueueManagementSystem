package com.nevmem.qms.fragments.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nevmem.qms.R
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
import com.nevmem.qms.utils.ifDebug
import kotlinx.android.synthetic.main.profile_avatar.view.*
import kotlinx.android.synthetic.main.profile_email.view.*
import kotlinx.android.synthetic.main.profile_header.view.*
import kotlinx.android.synthetic.main.profile_lastname.view.*
import kotlinx.android.synthetic.main.profile_name.view.*
import kotlinx.android.synthetic.main.profile_rating.view.*
import kotlinx.android.synthetic.main.profile_visited.view.*

internal class ProfileAvatarFactory(private val context: Context) : RVItemFactory {
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

internal class ProfileNameFactory(private val context: Context) : RVItemFactory {
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

internal class ProfileLastNameFactory(private val context: Context) : RVItemFactory {
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

internal class ProfileEmailFactory(private val context: Context) : RVItemFactory {
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

internal class ProfileVisitedFactory(
    private val context: Context,
    private val featureManager: FeatureManager
) : RVItemFactory {
    private inner class Holder(view: View) : RVHolder(view), FeatureManager.Listener {
        override fun onFeaturesUpdated() {
            itemView.tagsBox.isVisible =
                featureManager.getFeature("visited_tags_visible") == "visible"
        }

        override fun onBind(item: RVItem) {
            item as ProfileFragmentViewModel.ProfileVisitedPlace
            Glide.with(context)
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
    override fun createHolder(root: ViewGroup): RVHolder = Holder(context.inflate(R.layout.profile_visited, root))
}

internal class ProfileRatingFactory(private val context: Context) : RVItemFactory {
    private inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as ProfileFragmentViewModel.ProfileRating
            itemView.profileRating.text = "${item.rating}"
            if (item.rating >= 4) {
                itemView.profileRating.setTextColor(context.getColor(R.color.successColor))
            } else {
                itemView.profileRating.setTextColor(context.getColor(R.color.errorColor))
            }
        }
    }
    override fun isAppropriateType(item: RVItem): Boolean = item is ProfileFragmentViewModel.ProfileRating
    override fun createHolder(root: ViewGroup): RVHolder
            = Holder(context.inflate(R.layout.profile_rating, root))
}

internal class HeaderFactory(
    private val navController: NavController,
    private val context: Context,
    private val authManager: AuthManager
) : RVItemFactory {
    private inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            ifDebug {
                itemView.settingsButton.isVisible = true
                itemView.settingsButton.setOnClickListener {
                    val action = ProfileFragmentDirections.moveFromProfileToSettings()
                    navController.navigate(action)
                }
                itemView.logoutButton.setOnClickListener {
                    authManager.logout()
                    val action = ProfileFragmentDirections.moveToLoginOnLogout()
                    navController.navigate(action)
                }
            }
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is ProfileFragment.HeaderStub
    override fun createHolder(root: ViewGroup): RVHolder = Holder(context.inflate(R.layout.profile_header, root))
}
