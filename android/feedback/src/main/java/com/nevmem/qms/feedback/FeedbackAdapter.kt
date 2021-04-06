package com.nevmem.qms.feedback

import androidx.lifecycle.LiveData
import com.nevmem.qms.data.feedback.Feedback
import com.nevmem.qms.recycler.RVItem


interface FeedbackAdapter {
    sealed class State {
        object None : State()
        object Loading : State()
        data class Error(val message: String) : State()
        data class Data(val feedback: List<Feedback>) : State()
    }

    interface Listener {
        fun onStateUpdated()
    }

    val state: State
    val liveState: LiveData<State>

    fun stateToItems(state: State): List<RVItem>

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)
}
