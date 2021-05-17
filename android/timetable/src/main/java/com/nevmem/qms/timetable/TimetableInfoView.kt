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
            transition(Timetable(event.timetable))
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

    companion object {
        object StartLoadingEvent : Event()
        data class GotTimetable(val timetable: TimetableProto.Timetable) : Event()
        object TimetableError : Event()

        object None : State()
        object LoadingState : State()
        object Error : State()
        data class Timetable(val timetable: TimetableProto.Timetable) : State()
    }
}
