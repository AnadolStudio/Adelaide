package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.core.data_source.media.Folder
import com.anadolstudio.core.viewmodel.BaseController

interface GalleryController : BaseController {
    fun onPermissionGranted()
    fun onFolderChanged(folder: Folder)
    fun onImageSelected(imageUri: String)
    fun onNavigateToSettingsClicked()
    fun onLoadMoreImages()
    fun onZoomIncreased()
    fun onZoomDecreased()
    fun onRefreshed()
}
