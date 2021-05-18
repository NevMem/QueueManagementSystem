package com.nevmem.qms.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nevmem.qms.TimetableProto
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.BaseRecyclerAdapter
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory

class TimetableFragment : BottomSheetDialogFragment() {

    private val recycler: RecyclerView
        get() {
            return requireView().findViewById(R.id.timetableRecycler)
        }

    lateinit var timetable: TimetableProto.Timetable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_timetable_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.adapter = BaseRecyclerAdapter(
            TimetableProto.WorkInterval.WeekDay.values().toList()
                .filter { it != TimetableProto.WorkInterval.WeekDay.UNRECOGNIZED }
                .map {
                    WeekDayRvItem(it, timetable.worksList.filter { item -> item.weekday == it })
                },
            TimetableItemFactory())
    }

    data class WeekDayRvItem(
        val day: TimetableProto.WorkInterval.WeekDay,
        val items: List<TimetableProto.WorkInterval>
    ) : RVItem()

    inner class TimetableItemFactory : RVItemFactory {
        inner class Holder(view: View) : RVHolder(view) {
            private val itemsLayout: FlexboxLayout
                get() = itemView.findViewById(R.id.items)

            private val title: AppCompatTextView
                get() = itemView.findViewById(R.id.timetableItemTitle)

            override fun onBind(item: RVItem) {
                item as WeekDayRvItem

                title.text = item.day.toString()

                item.items.forEach {
                    requireContext().inflate(R.layout.layout_work_interval, itemView as ViewGroup).apply {
                        findViewById<AppCompatTextView>(R.id.fromTextView).text = "${it.from.hour}:${it.from.minute}"
                        findViewById<AppCompatTextView>(R.id.toTextView).text = "${it.to.hour}:${it.to.minute}"
                        itemsLayout.addView(this)
                    }
                }
            }
        }

        override fun isAppropriateType(item: RVItem): Boolean = item is WeekDayRvItem

        override fun createHolder(root: ViewGroup): RVHolder
            = Holder(requireContext().inflate(R.layout.layout_timetable_item, root))
    }

    companion object {
        fun newInstance(): TimetableFragment = TimetableFragment()
    }
}
