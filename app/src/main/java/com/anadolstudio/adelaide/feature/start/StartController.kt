package com.anadolstudio.adelaide.feature.start

import com.anadolstudio.core.viewmodel.BaseController

interface StartController : BaseController {
    fun onGalleryClicked()
    fun onTakePhotoClicked()
    fun onDraftClicked()
    fun onInfoClicked()
}
