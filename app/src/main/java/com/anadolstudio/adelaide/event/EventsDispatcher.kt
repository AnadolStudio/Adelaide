package com.anadolstudio.adelaide.event

internal interface EventsDispatcher {

    val events: EventQueue

    fun offerEvent(event: Event) {
        events.offerEvent(event)
    }
}
