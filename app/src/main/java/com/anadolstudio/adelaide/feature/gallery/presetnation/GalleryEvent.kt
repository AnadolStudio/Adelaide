package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.ui.viewmodel.livedata.SingleCustomEvent

sealed class GalleryEvent : SingleCustomEvent() {

    object RequestPermissionEvent : GalleryEvent()

}
