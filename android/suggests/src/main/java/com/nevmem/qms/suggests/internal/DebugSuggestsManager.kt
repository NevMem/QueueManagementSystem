package com.nevmem.qms.suggests.internal

import com.nevmem.qms.QueueProto
import com.nevmem.qms.suggests.Suggest
import com.nevmem.qms.suggests.SuggestsManager

internal class DebugSuggestsManager : SuggestsManager {

    private val listeners = mutableSetOf<SuggestsManager.Listener>()

    override var suggests: List<Suggest> = emptyList()

    init {
        suggests = (1..3).map { listOf(
            Suggest(QueueProto.Queue.newBuilder()
                .setName("Hospital #1")
                .setDescription("Top one hospital in country")
                .setImageUrl("http://www.fecalface.com/blogs/random/hospital/1.jpg")
                .build()),
            Suggest(QueueProto.Queue.newBuilder()
                .setName("Hospital #2")
                .setDescription("Top two hospital in country")
                .build()),
            Suggest(QueueProto.Queue.newBuilder()
                .setName("Hospital #3")
                .setDescription("Top three hospital in country")
                .build()),
            Suggest(QueueProto.Queue.newBuilder()
                .setName("Restaurant")
                .setDescription("Some restaurant")
                .setImageUrl("http://www.ondiseno.com/fotos_proyectos/360/grans/235301.jpg")
                .build()),
            Suggest(QueueProto.Queue.newBuilder()
                .setName("Hospital #4")
                .setDescription("Top for hospital in country")
                .build())) }.flatten()
    }

    override fun addListener(listener: SuggestsManager.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: SuggestsManager.Listener) {
        listeners.remove(listener)
    }
}
