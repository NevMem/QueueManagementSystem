package com.nevmem.qms.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class RVHolder(view: View): RecyclerView.ViewHolder(view) {
    abstract fun onBind(item: RVItem)
}