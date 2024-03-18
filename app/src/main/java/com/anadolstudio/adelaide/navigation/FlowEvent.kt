package com.licard.b2b.app.library.navigation

internal interface FlowEvent

internal interface FlowEventHandler {
    fun accept(event: FlowEvent)
}
