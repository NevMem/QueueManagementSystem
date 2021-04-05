package com.nevmem.qms.feedback.recycler.factory

import android.content.Context
import com.nevmem.qms.recycler.RVItemFactory

fun feedbackFactories(context: Context): List<RVItemFactory> = listOf(
    LoadingFeedbackItemFactory(context),
    ErrorFeedbackItemFactory(context),
    NoFeedbackItemFactory(context),
    FeedbackItemFactory(context)
)
