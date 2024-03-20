package com.anadolstudio.adelaide.feature.start.single

import com.anadolstudio.ui.viewmodel.livedata.SingleCustomEvent

sealed class SingleEvents : SingleCustomEvent() {

    data class ChangeNightModeEvent(val mode: Int) : SingleEvents()
}
