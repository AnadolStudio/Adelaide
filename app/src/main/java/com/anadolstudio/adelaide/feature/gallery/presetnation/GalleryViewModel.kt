package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.Manifest
import android.content.Context
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.viewmodel.BaseContentViewModel
import com.anadolstudio.adelaide.feature.common.domain.NightModeRepository
import com.anadolstudio.adelaide.feature.gallery.domain.GalleryRepository
import com.anadolstudio.paginator.PaginatorImpl
import com.anadolstudio.paginator.PagingDataState
import com.anadolstudio.paginator.PagingViewController
import com.anadolstudio.ui.viewmodel.states.LoadingDataContext
import com.anadolstudio.ui.viewmodel.states.smartSubscribeWithUpdatingState
import com.anadolstudio.utils.data_source.media.Folder
import com.anadolstudio.utils.util.extentions.hasAllPermissions
import com.anadolstudio.utils.util.extentions.hasAnyPermissions
import com.anadolstudio.utils.util.extentions.startAppSettingsActivity
import com.anadolstudio.utils.util.rx.schedulersIoToMain
import io.reactivex.Single
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class GalleryViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val nightModeRepository: NightModeRepository,
    private val context: Context,
) : BaseContentViewModel<GalleryState>(
    GalleryState(
        columnSpan = DEFAULT_COLUM_COUNT,
        pagingDataState = when (context.hasAnyPermissions(STORAGE_PERMISSION)) {
            true -> PagingDataState.Loading()
            false -> PagingDataState.Empty()
        },
    )
), GalleryController{

    companion object {
        private const val PAGE_SIZE = 66
        private const val FIRST_PAGE_NUMBER = 0
        private const val MAX_COLUM_COUNT = 4
        private const val MIN_COLUM_COUNT = 2
        private const val MIN_FOLDER_COUNT = 1

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
        viewController = pagingViewControllerDelegate,
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
        loadFolders(LoadingDataContext.INIT_LOADING)
        loadImage()
    }

    private fun loadFolders(loadingDataContext: LoadingDataContext) {
        galleryRepository.loadFolders()
            .map(this::mapFolders)
            .smartSubscribeWithUpdatingState(
                loadingContext = loadingDataContext,
                previousState = state.folderState.progressState,
                onNewState = { updateState { copy(folderState = folderState.copy(progressState = it)) } },
                onSuccess = { folders ->
                    val currentFolder =
                        folders.firstOrNull { it == state.folderState.currentFolder }
                            ?: folders.firstOrNull()

                    updateState {
                        copy(
                            folderState = folderState.copy(
                                currentFolder = currentFolder,
                                folders = folders
                            )
                        )
                    }
                },
                onError = this::showError,
            )
            .disposeOnCleared()
    }

    private fun mapFolders(folders: Set<Folder>): Set<Folder> {
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
        return folderList.toSet()
    }

    private fun loadImage() = paginator.restart()

    override fun onPermissionGranted() = initLoad()

    override fun onImageSelected(imageUri: String) =
        showEvent(GalleryEvent.DetailPhotoEvent(imageUri))

    override fun onLoadMoreImages() = paginator.loadNewPage()

    override fun onNavigateToSettingsClicked() = context.startAppSettingsActivity()

    override fun onBackClicked() {
        nightModeRepository.toggleNightMode()
    }
//    override fun onBackClicked() = navigateUp()

    override fun onFolderChanged(folder: Folder) {
        if (folder == state.folderState.currentFolder) return

        updateState { copy(folderState = folderState.copy(currentFolder = folder)) }
        paginator.pullToRefresh()
    }

    override fun onZoomIncreased() =
        updateState { copy(columnSpan = min(columnSpan + 1, MAX_COLUM_COUNT)) }

    override fun onZoomDecreased() =
        updateState { copy(columnSpan = max(columnSpan - 1, MIN_COLUM_COUNT)) }

    override fun onFolderClosed() =
        updateState { copy(folderState = folderState.copy(folderVisible = false)) }

    override fun onFolderOpened() = updateState {
        copy(folderState = folderState.copy(folderVisible = folderState.folders.isNotEmpty()))
    }

}
