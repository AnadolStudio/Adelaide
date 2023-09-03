package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.core.viewmodel.livedata.SingleCustomEvent

sealed class GalleryEvent : SingleCustomEvent()

object RequestPermission : GalleryEvent()

class MoveFolderEvent(val index: Int, val moveType: MoveType) : GalleryEvent()
