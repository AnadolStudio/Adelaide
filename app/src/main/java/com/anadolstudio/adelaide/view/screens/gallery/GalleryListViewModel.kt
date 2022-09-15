package com.anadolstudio.adelaide.view.screens.gallery

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.anadolstudio.adelaide.data.GalleryService
import com.anadolstudio.core.viewmodel.Communication

class GalleryListViewModel(
        private val galleryService: GalleryService
) : ViewModel() {

    val folders = Communication.UiUpdate<Result<Set<String>>>()
    val images = Communication.UiUpdate<Result<List<String>>>()

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    fun loadFolders(context: Context) {
        folders.map(Result.Loading())

        galleryService.loadFolders(context)
                .onSuccess { data -> folders.map(if (data.isEmpty()) Result.Empty() else Result.Success(data)) }
                .onError { ex -> folders.map(Result.Error(ex)) }
    }

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    fun loadImages(context: Context, folder: String? = null, lastItemId: Long = -1L) {
        images.map(Result.Loading())

        galleryService.loadImages(context, folder, lastItemId)
                .onSuccess { data -> images.map(if (data.isEmpty()) Result.Empty() else Result.Success(data)) }
                .onError { ex -> images.map(Result.Error(ex)) }
    }

}
