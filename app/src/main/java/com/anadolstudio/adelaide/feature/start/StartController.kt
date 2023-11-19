package com.anadolstudio.adelaide.feature.start

import android.net.Uri
import com.anadolstudio.core.viewmodel.BaseController

interface StartController : BaseController {
    fun onGalleryClicked()
    fun onDraftClicked()
    fun onInfoClicked()
    fun onCameraPermissionsGranted()
    fun onTakePhotoResult(uri: Uri?)
}
