package com.nevmem.qms.notifications

import androidx.annotation.IdRes

data class Notification(
    @IdRes val iconResourceId: Int,
    val title: String,
    val content: String
)
