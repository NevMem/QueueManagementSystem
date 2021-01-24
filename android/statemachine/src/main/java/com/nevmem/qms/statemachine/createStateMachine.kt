package com.nevmem.qms.statemachine

import com.nevmem.qms.statemachine.internal.SyncStateMachine


fun createSyncStateMachine(initialState: State): StateMachine = SyncStateMachine(initialState)
