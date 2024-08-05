package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.graphics.Bitmap
import com.anadolstudio.ui.viewmodel.livedata.SingleCustomEvent

sealed class GalleryEvent : SingleCustomEvent() {

    object RequestPermissionEvent : GalleryEvent()

    class PreviewPhotoEvent(val image: Bitmap) : GalleryEvent()
}
