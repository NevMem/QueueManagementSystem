package com.nevmem.qms.feedback.internal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nevmem.qms.common.utils.retry
import com.nevmem.qms.common.utils.runOnIO
import com.nevmem.qms.common.utils.runOnUi
import com.nevmem.qms.feedback.FeedbackAdapter
import com.nevmem.qms.feedback.FeedbackManager
import com.nevmem.qms.feedback.recycler.ErrorFeedbackItem
import com.nevmem.qms.feedback.recycler.FeedbackItem
import com.nevmem.qms.feedback.recycler.LoadingFeedbackItem
import com.nevmem.qms.recycler.RVItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class FeedbackAdapterImpl(
    private val feedbackManager: FeedbackManager,
    private val entityId: String
) : FeedbackAdapter {
    private val mutableState = MutableLiveData<FeedbackAdapter.State>(FeedbackAdapter.State.None)

    override val state: FeedbackAdapter.State
        get() = TODO("Not yet implemented")
    override val liveState: LiveData<FeedbackAdapter.State> = mutableState

    init {
        GlobalScope.launch(Dispatchers.Default) {
            runOnUi { mutableState.postValue(FeedbackAdapter.State.Loading) }
            runOnIO {
                try {
                    val result = retry({
                        feedbackManager.loadFeedback(entityId)
                    })
                    runOnUi { mutableState.postValue(FeedbackAdapter.State.Data(result)) }
                } catch(exception: Exception) {
                    runOnUi { mutableState.postValue(FeedbackAdapter.State.Error(exception.message ?: "")) }
                }
            }
        }
    }

    override fun stateToItems(state: FeedbackAdapter.State): List<RVItem> {
        return when(state) {
            FeedbackAdapter.State.None -> listOf()
            FeedbackAdapter.State.Loading -> listOf(LoadingFeedbackItem)
            is FeedbackAdapter.State.Error -> listOf(ErrorFeedbackItem(state.message))
            is FeedbackAdapter.State.Data -> state.feedback.map { FeedbackItem(it) }
        }
    }

    override fun addListener(listener: FeedbackAdapter.Listener) {
        TODO("Not yet implemented")
    }

    override fun removeListener(listener: FeedbackAdapter.Listener) {
        TODO("Not yet implemented")
    }
}
