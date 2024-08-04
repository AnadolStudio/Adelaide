package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.ui.viewmodel.BaseController
import com.anadolstudio.utils.data_source.media.Folder
import com.anadolstudio.utils.data_source.media.Image

interface GalleryController : BaseController {
    fun onPermissionGranted()
    fun onFolderChanged(folder: Folder)
    fun onImageSelected(image: Image)
    fun onNavigateToSettingsClicked()
    fun onLoadMoreImages()
    fun onZoomIncreased()
    fun onZoomDecreased()
    fun onFolderClosed()
    fun onFolderOpened()
}
