package com.anadolstudio.adelaide.view.screens.gallery

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.anadolstudio.adelaide.data.GalleryListener
import com.anadolstudio.adelaide.data.GalleryService
import com.anadolstudio.core.tasks.Result
import com.anadolstudio.core.viewmodel.Communication

class GalleryListViewModel(
    private val galleryService: GalleryService
) : ViewModel() {

    val folders = Communication.UiUpdate<Result<Set<String>>>()
    val images = Communication.UiUpdate<Result<List<String>>>()

    private var imagesResult: Result<List<String>> =
        Result.Empty()
        set(value) {
            val b = field != value
            field = value
            if (b) notifyImageUpdates()
        }

    private var foldersResult: Result<Set<String>> =
        Result.Empty()
        set(value) {
            val b = field != value
            field = value
            if (b) notifyFoldersUpdates()
        }

    private val listener: GalleryListener = { folders, images ->
        foldersResult = if (folders.isEmpty()) Result.Empty() else Result.Success(folders)
        imagesResult = if (images.isEmpty()) Result.Empty() else Result.Success(images)
    }

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    fun loadData(activity: AppCompatActivity) {
        galleryService.addListener(listener)

        loadFolders(activity)
        loadImages(activity)
    }

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    fun loadFolders(activity: AppCompatActivity) {
        folders.map(Result.Loading())
        galleryService.loadFolders(activity).onError {
            folders.map(Result.Error(it))
        }
    }

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    fun loadImages(activity: AppCompatActivity, folder: String? = null, lastItemId: Long = -1L) {
        images.map(Result.Loading())
        galleryService.loadImages(activity, folder, lastItemId)
            .onError { images.map(Result.Error(it)) }
    }

    override fun onCleared() {
        super.onCleared()
        galleryService.removeListener(listener)
    }

    private fun notifyImageUpdates() {
        images.map(imagesResult)
    }

    private fun notifyFoldersUpdates() {
        folders.map(foldersResult)
    }
}