package com.nevmem.qms.recycler

import android.view.ViewGroup

interface RVItemFactory {
    fun isAppropriateType(item: RVItem): Boolean
    fun createHolder(root: ViewGroup): RVHolder
}