package com.anadolstudio.adelaide.view.screens.gallery

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.lifecycle.MutableLiveData
import com.anadolstudio.core.livedata.onNext
import com.anadolstudio.core.livedata.toImmutable
import com.anadolstudio.core.viewmodel.BaseViewModel
import com.anadolstudio.data.repository.GalleryRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import kotlin.math.max

class GalleryListViewModel(private val galleryRepository: GalleryRepository) : BaseViewModel() {

    private companion object {
        const val ONE_PORTION = 99
    }

    private var currentFolder: String? = null
    private val _screenState = MutableLiveData<GalleryScreenState>(GalleryScreenState.Empty)
    val screenState = _screenState.toImmutable()

    private var lastImageItem: Long? = null

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    fun loadData(context: Context) {
        _screenState.onNext(GalleryScreenState.Loading)

        Single.zip(
                galleryRepository.loadImages(context = context, size = ONE_PORTION, folder = null),
                galleryRepository.loadFolders(context)
        ) { images, folders -> GalleryScreenState.Content(images = images, folders = folders) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { data -> setScreenState(data) },
                        { error -> _screenState.onNext(GalleryScreenState.Error(error)) }
                )
                .disposeOnViewModelDestroy()
    }

    private fun setScreenState(data: GalleryScreenState.Content) {
        when (data.images.isEmpty()) {
            true -> if (!data.isLoadMore) _screenState.onNext(GalleryScreenState.Empty)
            false -> updateLastItem(data).also { _screenState.onNext(data) }
        }
    }

    private fun updateLastItem(data: GalleryScreenState.Content) {
        lastImageItem = Uri.parse(data.images.last()).path?.let { File(it).name }
                ?.toLongOrNull()
    }

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    fun loadImages(context: Context, size: Int = ONE_PORTION, loadMore: Boolean = false) {
        val lastItem = if (loadMore) lastImageItem else null

        galleryRepository.loadImages(context = context, size = size, folder = currentFolder, lastItemIndex = lastItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { images -> setScreenState(GalleryScreenState.Content(images = images, folders = null, loadMore)) },
                        { error -> _screenState.onNext(GalleryScreenState.Error(error)) },
                )
                .disposeOnViewModelDestroy()
    }

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    fun updateImages(context: Context, size: Int?) {
        if (_screenState.value !is GalleryScreenState.Content) return

        loadImages(context = context, size = max(size ?: 0, ONE_PORTION), loadMore = false)
    }

    fun folderChanged(folder: String?): Boolean = (currentFolder != folder).also { currentFolder = folder }

}
