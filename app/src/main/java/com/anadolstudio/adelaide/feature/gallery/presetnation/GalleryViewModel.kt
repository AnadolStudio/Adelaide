package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.Manifest
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.viewmodel.BaseContentViewModel
import com.anadolstudio.adelaide.base.viewmodel.navigateUp
import com.anadolstudio.adelaide.di.DI
import com.anadolstudio.adelaide.feature.gallery.domain.GalleryRepository
import com.anadolstudio.adelaide.feature.start.EditType
import com.anadolstudio.core.data_source.media.Folder
import com.anadolstudio.core.util.common_extention.hasAllPermissions
import com.anadolstudio.core.util.common_extention.hasAnyPermissions
import com.anadolstudio.core.util.common_extention.startAppSettingsActivity
import com.anadolstudio.core.util.paginator.PaginatorImpl
import com.anadolstudio.core.util.paginator.PagingDataState
import com.anadolstudio.core.util.paginator.PagingViewController
import com.anadolstudio.core.util.rx.lceSubscribe
import com.anadolstudio.core.util.rx.schedulersIoToMain
import io.reactivex.Single
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class GalleryViewModel(
        private val galleryRepository: GalleryRepository,
        private val context: Context,
        editType: EditType,
) : BaseContentViewModel<GalleryState>(
        GalleryState(
                editType = editType,
                columnSpan = DEFAULT_COLUM_COUNT,
                pagingDataState = when (context.hasAnyPermissions(STORAGE_PERMISSION)) {
                    true -> PagingDataState.Loading()
                    false -> PagingDataState.Empty()
                },
        )
), GalleryController, PagingViewController<String> {

    companion object {
        private const val PAGE_SIZE = 66
        private const val FIRST_PAGE_NUMBER = 0
        private const val MAX_COLUM_COUNT = 4
        private const val MIN_COLUM_COUNT = 2
        private const val MIN_FOLDER_COUNT = 1

        const val EDIT_TYPE_KEY = "editType"
        const val DEFAULT_COLUM_COUNT = 3

        val STORAGE_PERMISSION = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    private val requestFactory: ((Int) -> Single<List<String>>) = { pageIndex ->
        galleryRepository
                .loadImages(
                        pageIndex = pageIndex,
                        pageSize = PAGE_SIZE,
                        folder = state.folderState.currentFolder?.value,
                )
                .schedulersIoToMain()
    }

    private val pagingViewControllerDelegate = PagingViewController.Delegate(
            provideCurrentData = { state.imageState.imageList },
            provideCurrentPagingData = { state.imageState.pagingDataState },
            updateStateAction = { updateState { copy(imageState = imageState.copy(pagingDataState = it)) } },
            updateData = { updateState { copy(imageState = imageState.copy(imageList = it)) } }
    )

    private val paginator = PaginatorImpl(
            requestFactory = requestFactory,
            viewController = this,
            firstPageNumber = FIRST_PAGE_NUMBER
    )

    init {
        checkPermissionAndLoad()
    }

    private fun checkPermissionAndLoad() {
        if (context.hasAllPermissions(STORAGE_PERMISSION)) {
            initLoad()
        } else {
            showEvent(GalleryEvent.RequestPermissionEvent)
        }
    }

    private fun initLoad() {
        loadFolders()
        loadImage()
    }

    private fun loadFolders(onContent: (() -> Unit)? = null) {
        galleryRepository.loadFolders()
                .map { folders ->
                    val folderList = folders.filter { it.imageCount > 0 }.toMutableList()

                    if (folderList.size > MIN_FOLDER_COUNT) {
                        val totalCount = folderList.sumOf { it.imageCount }
                        val defaultFolder = folderList.first().copy(
                                name = context.getString(R.string.gallery_toolbar_title),
                                value = null,
                                imageCount = totalCount
                        )
                        folderList.add(0, defaultFolder)
                    }

                    return@map folderList.toSet()
                }
                .lceSubscribe(
                        onEach = { state ->
                            updateState { copy(folderState = folderState.copy(foldersLce = state)) }
                        },
                        onContent = { folders ->
                            val currentFolder = folders.firstOrNull { it == state.folderState.currentFolder }
                                    ?: folders.firstOrNull()

                            updateState {
                                copy(folderState = folderState.copy(currentFolder = currentFolder, folders = folders))
                            }
                            onContent?.invoke()
                        },
                        onError = this::showError
                )
                .disposeOnCleared()
    }

    private fun loadImage() = paginator.restart()

    override fun onLoading() = pagingViewControllerDelegate.onLoading()
    override fun onError(error: Throwable) = pagingViewControllerDelegate.onError(error)
    override fun onEmptyData() = pagingViewControllerDelegate.onEmptyData()
    override fun onPageData(data: List<String>) = pagingViewControllerDelegate.onPageData(data)
    override fun onNextPageError(error: Throwable) = pagingViewControllerDelegate.onNextPageError(error)
    override fun onNextPageLoading() = pagingViewControllerDelegate.onNextPageLoading()
    override fun onAllData() = pagingViewControllerDelegate.onAllData()
    override fun onRefresh() = pagingViewControllerDelegate.onRefresh()
    override fun onRefreshError(exception: Throwable) = pagingViewControllerDelegate.onRefreshError(exception)
    override fun onUpdateData(data: List<String>) = pagingViewControllerDelegate.onUpdateData(data)

    override fun onPermissionGranted() = initLoad()

    override fun onImageSelected(imageUri: String) = showEvent(GalleryEvent.DetailPhotoEvent(imageUri))

    override fun onLoadMoreImages() = paginator.loadNewPage()

    override fun onNavigateToSettingsClicked() = context.startAppSettingsActivity()

    override fun onBackClicked() = _navigationEvent.navigateUp()

    override fun onFolderChanged(folder: Folder) {
        if (folder == state.folderState.currentFolder) return

        updateState { copy(folderState = folderState.copy(currentFolder = folder)) }
        paginator.pullToRefresh()
    }

    override fun onZoomIncreased() = updateState { copy(columnSpan = min(columnSpan + 1, MAX_COLUM_COUNT)) }

    override fun onZoomDecreased() = updateState { copy(columnSpan = max(columnSpan - 1, MIN_COLUM_COUNT)) }

    override fun onFolderClosed() = updateState { copy(folderState = folderState.copy(folderVisible = false)) }

    override fun onFolderOpened() = updateState {
        copy(folderState = folderState.copy(folderVisible = folderState.folders.isNotEmpty()))
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val editType: EditType) : ViewModelProvider.NewInstanceFactory() {

        @Inject
        lateinit var context: Context

        @Inject
        lateinit var galleryRepository: GalleryRepository

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            DI.appComponent.inject(this)
            return GalleryViewModel(
                    galleryRepository = galleryRepository,
                    context = context,
                    editType = editType
            ) as T
        }
    }
}
