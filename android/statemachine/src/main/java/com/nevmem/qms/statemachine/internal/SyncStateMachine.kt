package com.nevmem.qms.statemachine.internal

import com.nevmem.qms.statemachine.*

typealias Handler = StateMachine.(Event) -> Boolean

internal class SyncStateMachine(initialState: State) : StateMachine() {

    override var currentState: State = initialState
        set(value) {
            if (value == field)
                return
            field = value
            delegate?.invoke(field)
        }

    private var delegate: StateChangedDelegate? = null

    private val handlers = mutableMapOf<Class<*>, MutableList<Handler>>()

    override fun <T : State> generalHandler(state: Class<T>, handle: Handler) {
        if (state in handlers) {
            handlers[state]!!.add(handle)
        } else {
            handlers[state] = mutableListOf(handle)
        }
    }

    override fun <T : State> state(state: Class<T>, block: StateMachineAddHandlerSession.() -> Unit)
        = block(object : StateMachineAddHandlerSession() {
            override fun generalHandler(handle: Handler) {
                generalHandler(state, handle)
            }
        })

    override fun transition(to: State): Boolean {
        currentState = to
        return true
    }

    override fun dispatchEvent(event: Event) = handle(event)

    override fun setStateDelegate(delegate: StateChangedDelegate) {
        this.delegate = delegate
    }

    override fun removeDelegate() {
        this.delegate = null
    }

    private fun handle(event: Event) {
        println("cur_deb got event $event")
        handlers[currentState.javaClass]?.forEach { handler ->
            if (handler(this, event)) {
                return
            }
        }
    }
}
