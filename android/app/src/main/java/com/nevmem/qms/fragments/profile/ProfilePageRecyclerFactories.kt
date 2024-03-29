package com.nevmem.qms.fragments.profile

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nevmem.qms.R
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.dialogs.DialogsManager
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.features.isFeatureEnabled
import com.nevmem.qms.inflate
import com.nevmem.qms.knownfeatures.KnownFeatures
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
import com.nevmem.qms.usecase.user.ChangeUserAvatarUsecaseFactory
import com.nevmem.qms.utils.ifDebug
import kotlinx.android.synthetic.main.layout_profile_loading_stub.view.*
import kotlinx.android.synthetic.main.profile_avatar.view.*
import kotlinx.android.synthetic.main.profile_email.view.*
import kotlinx.android.synthetic.main.profile_header.view.*
import kotlinx.android.synthetic.main.profile_lastname.view.*
import kotlinx.android.synthetic.main.profile_name.view.*
import kotlinx.android.synthetic.main.profile_rating.view.*
import kotlinx.android.synthetic.main.profile_visited.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class ProfileAvatarFactory(
    private val context: Context,
    private val dialogsManager: DialogsManager,
    private val usecaseFactory: ChangeUserAvatarUsecaseFactory
) : RVItemFactory {
    private inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as ProfileAvatar
            if (item.avatarUrl != null) {
                Glide.with(context)
                    .load(item.avatarUrl)
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.progress))
                    .apply(RequestOptions().circleCrop())
                    .into(itemView.avatarImage)
            } else {
                itemView.avatarImage.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.icon_profile))
            }
            itemView.avatarImage.setOnClickListener {
                GlobalScope.launch {
                    val result = dialogsManager.showTextInputDialog(
                        context.getString(R.string.change_avatar),
                        context.getString(R.string.new_avatar_url))
                    if (result is DialogsManager.TextInputDialogResolution.Text) {
                        val text = result.text
                        val usecase = usecaseFactory.createUsecase()
                        dialogsManager.showOperationStatusDialog(usecase.changeAvatar(text))
                    }
                }
            }
        }
    }
    override fun isAppropriateType(item: RVItem): Boolean = item is ProfileAvatar
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(LayoutInflater.from(context).inflate(R.layout.profile_avatar, root, false))
}

internal class ProfileNameFactory(private val context: Context) : RVItemFactory {
    private inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as ProfileName
            itemView.profileName.text = item.name
        }
    }
    override fun isAppropriateType(item: RVItem): Boolean = item is ProfileName
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(LayoutInflater.from(context).inflate(R.layout.profile_name, root, false))
}

internal class ProfileLastNameFactory(private val context: Context) : RVItemFactory {
    private inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as ProfileLastName
            itemView.profileLastName.text = item.lastName
        }
    }
    override fun isAppropriateType(item: RVItem): Boolean = item is ProfileLastName
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(LayoutInflater.from(context).inflate(R.layout.profile_lastname, root, false))
}

internal class ProfileEmailFactory(private val context: Context) : RVItemFactory {
    private inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as ProfileEmail
            itemView.profileEmail.text = item.email
        }
    }
    override fun isAppropriateType(item: RVItem): Boolean = item is ProfileEmail
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
                featureManager.isFeatureEnabled(KnownFeatures.VisitedTags.value)
        }

        override fun onBind(item: RVItem) {
            item as ProfileVisitedPlace
            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(itemView.placeIcon)
            itemView.visitedOrganizationName.text = item.organization.info.name
            itemView.visitedServiceName.text = item.service.info.name
            itemView.tagsBox.setTags(item.tags)

            itemView.setOnClickListener {
                val intent = NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.navigation)
                    .setDestination(R.id.navigation_join)
                    .setArguments(Bundle().apply {
                        putString("invite_id", item.organization.info.id)
                    })
                    .createPendingIntent()
                intent.send()
            }

            itemView.doOnAttach {
                featureManager.addListener(this)
                onFeaturesUpdated()

                itemView.doOnDetach {
                    featureManager.removeListener(this)
                }
            }
        }
    }
    override fun isAppropriateType(item: RVItem): Boolean = item is ProfileVisitedPlace
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.profile_visited, root))
}

internal class ProfileRatingFactory(private val context: Context) : RVItemFactory {
    private inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as ProfileRating
            itemView.profileRating.text = "${item.rating}"
            if (item.rating >= 4) {
                itemView.profileRating.setTextColor(context.getColor(R.color.successColor))
            } else {
                itemView.profileRating.setTextColor(context.getColor(R.color.errorColor))
            }
        }
    }
    override fun isAppropriateType(item: RVItem): Boolean = item is ProfileRating
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

    override fun isAppropriateType(item: RVItem): Boolean = item is HeaderStub
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.profile_header, root))
}

internal class SpaceStubFactory(
    private val context: Context
) : RVItemFactory {
    private inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) = Unit
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is SpaceStub
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.layout_space_stub, root))
}

internal class ProfileLoadingStubFactory(
    private val context: Context
) :RVItemFactory {
    private inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            val animator = ValueAnimator.ofFloat(1f, 0.5f, 1f).apply {
                repeatCount = ValueAnimator.INFINITE
                duration = 1000L

                addUpdateListener {
                    itemView.profileLoadingStubIcon.alpha = it.animatedValue as Float
                }
            }

            itemView.doOnAttach {
                animator.start()
                it.doOnDetach {
                    animator.cancel()
                }
            }
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is ProfileLoadingStub
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.layout_profile_loading_stub, root))
}
