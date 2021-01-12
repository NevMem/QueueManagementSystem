package com.nevmem.qms.fragments.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nevmem.qms.R
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
import kotlinx.android.synthetic.main.profile_avatar.view.*
import kotlinx.android.synthetic.main.profile_email.view.*
import kotlinx.android.synthetic.main.profile_lastname.view.*
import kotlinx.android.synthetic.main.profile_name.view.*

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
