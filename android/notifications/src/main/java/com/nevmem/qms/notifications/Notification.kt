package com.nevmem.qms.notifications

import androidx.annotation.DrawableRes

data class Notification(
    @DrawableRes val iconResourceId: Int,
    val title: String,
    val content: String
)
