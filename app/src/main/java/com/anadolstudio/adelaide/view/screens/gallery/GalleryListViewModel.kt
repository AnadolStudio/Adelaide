package com.anadolstudio.adelaide.view.screens.gallery

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anadolstudio.adelaide.data.GalleryListener
import com.anadolstudio.adelaide.data.GalleryService
import com.anadolstudio.core.tasks.Result

class GalleryListViewModel(
    private val galleryService: GalleryService
) : ViewModel() {

    private val _folders = MutableLiveData<Result<Set<String>>>()
    val folders: LiveData<Result<Set<String>>> = _folders

    private val _images = MutableLiveData<Result<List<String>>>()
    val images: LiveData<Result<List<String>>> = _images

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
        _folders.value = Result.Loading()
        galleryService.loadFolders(activity).onError {
            _folders.value = Result.Error(it)
        }
    }

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    fun loadImages(activity: AppCompatActivity, folder: String? = null, lastItemId: Long = -1L) {
        _images.value = Result.Loading()
        galleryService.loadImages(activity, folder, lastItemId)
            .onError { _images.value = Result.Error(it) }
    }

    override fun onCleared() {
        super.onCleared()
        galleryService.removeListener(listener)
    }

    private fun notifyImageUpdates() {
        _images.postValue(imagesResult)
    }

    private fun notifyFoldersUpdates() {
        _folders.postValue(foldersResult)
    }
}