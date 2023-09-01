package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.core.viewmodel.BaseController

interface GalleryController : BaseController {
    fun onPermissionGranted()
    fun onFolderChanged(folderName: String)
    fun onImageSelected(imageUri: String)
    fun onNavigateToSettingsClicked()
    fun onLoadMoreImages()
    fun onZoomIncreased()
    fun onZoomDecreased()
}
