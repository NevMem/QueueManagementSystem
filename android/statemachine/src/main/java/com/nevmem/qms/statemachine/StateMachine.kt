package com.nevmem.qms.statemachine

abstract class State
abstract class Event

abstract class StateMachineAddHandlerSession {
    abstract fun generalHandler(handle: StateMachine.(Event) -> Boolean)
    inline fun<reified E> handler(crossinline handle: StateMachine.(E) -> Boolean) {
        generalHandler { event ->
            if (event is E) {
                handle(event)
            } else {
                false
            }
        }
    }
}

typealias StateChangedDelegate = (State) -> Unit

abstract class StateMachine {
    abstract val currentState: State

    abstract fun<T : State> generalHandler(state: Class<T>, handle: StateMachine.(Event) -> Boolean)

    inline fun<T : State, reified E> handler(state: Class<T>, crossinline handle: StateMachine.(E) -> Boolean) {
        generalHandler(state) { event ->
            if (event is E) {
                handle(event)
            } else {
                false
            }
        }
    }

    abstract fun<T : State> state(state: Class<T>, block: StateMachineAddHandlerSession.() -> Unit)

    abstract fun dispatchEvent(event: Event)

    abstract fun transition(to: State): Boolean

    abstract fun setStateDelegate(delegate: StateChangedDelegate)
    abstract fun removeDelegate()
}
