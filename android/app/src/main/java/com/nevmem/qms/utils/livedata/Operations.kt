package com.nevmem.qms.utils.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun<A: Any, B: Any> combineLatest(first: LiveData<A>, second: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastFromFirst: A? = null
        var lastFromSecond: B? = null

        val process = {
            if (lastFromFirst != null && lastFromSecond != null) {
                postValue(lastFromFirst!! to lastFromSecond!!)
            }
        }

        addSource(first) {
            lastFromFirst = it
            process()
        }
        addSource(second) {
            lastFromSecond = it
            process()
        }
    }
}

fun<A: Any> mergeLatest(first: LiveData<List<A>>, second: LiveData<List<A>>): LiveData<List<A>> {
    return MediatorLiveData<List<A>>().apply {
        var lastFromFirst: List<A>? = null
        var lastFromSecond: List<A>? = null

        val process = {
            if (lastFromFirst != null && lastFromSecond != null) {
                postValue(lastFromFirst!!.toMutableList() + lastFromSecond!!.toMutableList())
            }
        }

        addSource(first) {
            lastFromFirst = it
            process()
        }
        addSource(second) {
            lastFromSecond = it
            process()
        }
    }
}

fun<A : Any> mergeLatestWithEmpty(first: LiveData<List<A>>, second: LiveData<List<A>>): LiveData<List<A>> {
    return MediatorLiveData<List<A>>().apply {
        var lastFromFirst: List<A>? = null
        var lastFromSecond: List<A>? = null

        val process = {
            postValue((lastFromFirst?.toMutableList() ?: emptyList<A>()) + (lastFromSecond?.toMutableList() ?: emptyList<A>()))
        }

        addSource(first) {
            lastFromFirst = it
            process()
        }
        addSource(second) {
            lastFromSecond = it
            process()
        }
    }
}
