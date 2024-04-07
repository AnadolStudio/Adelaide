package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.ui.viewmodel.BaseController
import com.anadolstudio.utils.data_source.media.Folder

interface MediaController : BaseController {
    fun onPermissionGranted()
    fun onFolderChanged(folder: Folder)
    fun onImageSelected(imageUri: String)
    fun onNavigateToSettingsClicked()
    fun onLoadMoreImages()
    fun onZoomIncreased()
    fun onZoomDecreased()
    fun onFolderClosed()
    fun onFolderOpened()
    fun onSearchClicked()
    fun onMoreClicked()
}
