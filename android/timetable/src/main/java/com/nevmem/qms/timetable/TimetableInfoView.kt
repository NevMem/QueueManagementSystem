package com.nevmem.qms.timetable

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.FrameLayout
import com.nevmem.qms.TimetableProto
import com.nevmem.qms.common.utils.runOnUi
import com.nevmem.qms.dialogs.FragmentManagerProvider
import com.nevmem.qms.inflate
import com.nevmem.qms.organizations.OrganizationsRepo
import com.nevmem.qms.organizations.exceptions.toOrganizationInfo
import com.nevmem.qms.statemachine.Event
import com.nevmem.qms.statemachine.State
import com.nevmem.qms.statemachine.StateMachine
import com.nevmem.qms.statemachine.createSyncStateMachine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class TimetableInfoView : FrameLayout {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defStyle: Int) : super(ctx, attrs, defStyle)

    private data class Dependencies(
        val fragmentManagerProvider: FragmentManagerProvider,
        val organizationsRepo: OrganizationsRepo,
        val organizationId: String
    )

    private lateinit var deps: Dependencies
    private lateinit var machine: StateMachine

    fun provideDependencies(
        fragmentManagerProvider: FragmentManagerProvider,
        organizationsRepo: OrganizationsRepo,
        organizationId: String
    ) {
        deps = Dependencies(
            fragmentManagerProvider,
            organizationsRepo,
            organizationId)
        initializeStateMachine()
        viewInitialized()
    }

    private fun initializeStateMachine() {
        machine = createSyncStateMachine(None)

        machine.handler(None::class.java) { _: StartLoadingEvent ->
            transition(LoadingState)
        }

        machine.handler(LoadingState::class.java) { _: TimetableError ->
            transition(Error)
        }

        machine.handler(LoadingState::class.java) { event: GotTimetable ->
            if (event.timetable.workingNow) {
                transition(Timetable(event.timetable))
            } else {
                transition(TimetableClosed(event.timetable))
            }
        }

        machine.setStateDelegate {
            removeAllViews()
            if (it is LoadingState) {
                addView(context.inflate(R.layout.layout_loading, this@TimetableInfoView))
            } else if (it is Timetable) {
                val timetable = it.timetable
                if (timetable.worksList.isNotEmpty()) {
                    val view = context.inflate(R.layout.layout_show_timetable)
                    view.findViewById<Button>(R.id.showTimetableButton).apply {
                        setOnClickListener {
                            deps.fragmentManagerProvider.provideFragmentManager().let { fm ->
                                val fragment = TimetableFragment.newInstance()
                                fragment.timetable = timetable
                                fragment.show(fm, "timetable")
                            }
                        }
                    }
                    addView(view)
                }
            } else if (it is TimetableClosed) {
                val timetable = it.timetable
                if (timetable.worksList.isNotEmpty()) {
                    val view = context.inflate(R.layout.layout_not_working)
                    view.findViewById<Button>(R.id.showTimetableButton).apply {
                        setOnClickListener {
                            deps.fragmentManagerProvider.provideFragmentManager().let { fm ->
                                val fragment = TimetableFragment.newInstance()
                                fragment.timetable = timetable
                                fragment.show(fm, "timetable")
                            }
                        }
                    }
                    addView(view)
                }
            }
        }
    }

    private fun viewInitialized() {
        machine.dispatchEvent(StartLoadingEvent)

        GlobalScope.launch(Dispatchers.IO) {
            val organization = deps.organizationsRepo.findOrganization(deps.organizationId.toOrganizationInfo())
            val timetable = organization.info.timetable

            if (timetable != null) {
                runOnUi { machine.dispatchEvent(GotTimetable(timetable)) }
            } else {
                runOnUi { machine.dispatchEvent(TimetableError) }
            }
        }
    }

    private val TimetableProto.Timetable.workingNow: Boolean
        get() {
            var working = false
            val day = {
                when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> TimetableProto.WorkInterval.WeekDay.MON
                    Calendar.TUESDAY -> TimetableProto.WorkInterval.WeekDay.TUE
                    Calendar.THURSDAY -> TimetableProto.WorkInterval.WeekDay.THU
                    Calendar.WEDNESDAY -> TimetableProto.WorkInterval.WeekDay.WED
                    Calendar.FRIDAY -> TimetableProto.WorkInterval.WeekDay.FRI
                    Calendar.SATURDAY -> TimetableProto.WorkInterval.WeekDay.SAT
                    Calendar.SUNDAY -> TimetableProto.WorkInterval.WeekDay.SUN
                    else -> TimetableProto.WorkInterval.WeekDay.UNRECOGNIZED
                }
            }()
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val minute = Calendar.getInstance().get(Calendar.MINUTE)
            val curTimeMarker = TimetableProto.WorkInterval.TimeMarker.newBuilder()
                .setHour(hour)
                .setMinute(minute)
                .build()
            worksList.filter { it.weekday == day }
                .forEach {
                    if (it.from <= curTimeMarker && curTimeMarker <= it.to) {
                        working = true
                    }
                }
            return working
        }

    operator fun TimetableProto.WorkInterval.TimeMarker.compareTo(obj: Any): Int {
        val cp = (obj as? TimetableProto.WorkInterval.TimeMarker) ?: return -1
        if (hour == cp.hour) {
            if (minute < cp.minute) {
                return -1
            } else if (minute == cp.minute) {
                return 0
            }
            return 1
        }
        if (hour < cp.hour) {
            return -1
        }
        return 1
    }

    companion object {
        object StartLoadingEvent : Event()
        data class GotTimetable(val timetable: TimetableProto.Timetable) : Event()
        object TimetableError : Event()

        object None : State()
        object LoadingState : State()
        object Error : State()
        data class Timetable(val timetable: TimetableProto.Timetable) : State()
        data class TimetableClosed(val timetable: TimetableProto.Timetable) : State()
    }
}
