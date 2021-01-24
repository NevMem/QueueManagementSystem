package com.nevmem.qms.statemachine

import org.junit.Test

class SimpleSyncStateMachineTests {
    @Test
    fun justCreate() {
        class State1 : State()
        val machine = createSyncStateMachine(State1())
        check(machine.currentState is State1) { "Initial state class doesn't match" }
    }

    @Test
    fun createAndSimpleTransition() {
        class State1 : State()
        class State2 : State()
        class Event1 : Event()
        val machine = createSyncStateMachine(State1())
        machine.state(State1::class.java) {
            handler { event: Event1 ->
                transition(State2())
                true
            }
        }
        check(machine.currentState is State1) { "Initial state class doesn't match" }
        machine.dispatchEvent(Event1())
        check(machine.currentState is State2) { "State didn't changed" }
        machine.dispatchEvent(Event1())
        check(machine.currentState is State2) { "State unexpectedly changed" }
    }

    @Test
    fun testWithComplexState() {
        class State1 : State()
        data class State2(val counter: Int) : State()
        class EndState : State()

        class Event1 : Event()

        val machine = createSyncStateMachine(State1())
        machine.state(State1::class.java) {
            handler { event: Event1 ->
                transition(State2(10))
                true
            }
        }

        machine.handler(State2::class.java) { event: Event1 ->
            val curState = machine.currentState as State2
            if (curState.counter == 0) {
                transition(EndState())
            } else {
                transition(State2(curState.counter - 1))
            }
            true
        }

        machine.dispatchEvent(Event1())
        check(machine.currentState is State2)
        repeat(10) {
            machine.dispatchEvent(Event1())
            check(machine.currentState is State2)
        }
        machine.dispatchEvent(Event1())
        check(machine.currentState is EndState)
    }
}
