package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.ui.viewmodel.livedata.SingleCustomEvent

sealed class MediaEvent : SingleCustomEvent() {

    object RequestPermissionEvent : MediaEvent()

}
