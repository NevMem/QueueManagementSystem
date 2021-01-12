package com.nevmem.qms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun Context.inflate(@LayoutRes id: Int): View
    = LayoutInflater.from(this).inflate(id, null, false)

fun Context.inflate(@LayoutRes id: Int, parent: ViewGroup): View
    = LayoutInflater.from(this).inflate(id, parent, false)
