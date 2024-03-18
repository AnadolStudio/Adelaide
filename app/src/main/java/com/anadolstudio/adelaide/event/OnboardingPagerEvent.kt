package com.licard.b2b.app.base.presentation.event

import com.anadolstudio.adelaide.event.Event

internal data class OnboardingPagerEvent(val newPageNumber: Int) : Event
