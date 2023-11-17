package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.core.viewmodel.livedata.SingleCustomEvent

sealed class GalleryEvent : SingleCustomEvent()

object RequestPermission : GalleryEvent()
