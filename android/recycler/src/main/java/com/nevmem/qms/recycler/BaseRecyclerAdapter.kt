package com.nevmem.qms.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BaseRecyclerAdapter(
    private val list: List<RVItem>,
    vararg factories: RVItemFactory
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemFactories: List<RVItemFactory> = factories.toList()

    override fun getItemViewType(position: Int): Int {
        val item = list[position]
        val index = itemFactories.indexOfFirst { it.isAppropriateType(item) }
        check(index != -1) { "Not found factory for item $item" }
        return index
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return itemFactories[viewType].createHolder(parent)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as RVHolder
        holder.onBind(list[position])
    }
}