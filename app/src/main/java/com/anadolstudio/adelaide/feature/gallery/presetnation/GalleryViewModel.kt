package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.Manifest
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anadolstudio.adelaide.base.viewmodel.BaseContentViewModel
import com.anadolstudio.adelaide.base.viewmodel.navigateUp
import com.anadolstudio.adelaide.di.DI
import com.anadolstudio.adelaide.feature.gallery.presetnation.model.Folder
import com.anadolstudio.adelaide.feature.gallery.presetnation.model.toFolder
import com.anadolstudio.adelaide.feature.start.EditType
import com.anadolstudio.core.util.common_extention.hasAllPermissions
import com.anadolstudio.core.util.common_extention.hasAnyPermissions
import com.anadolstudio.core.util.common_extention.startAppSettingsActivity
import com.anadolstudio.core.util.paginator.PaginatorImpl
import com.anadolstudio.core.util.paginator.PagingDataState
import com.anadolstudio.core.util.paginator.PagingViewController
import com.anadolstudio.core.util.rx.lceSubscribe
import com.anadolstudio.core.util.rx.schedulersIoToMain
import com.anadolstudio.core.viewmodel.livedata.onNext
import com.anadolstudio.data.repository.GalleryRepository
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
                imageListState = when (context.hasAnyPermissions(STORAGE_PERMISSION)) {
                    true -> PagingDataState.Loading()
                    false -> PagingDataState.Empty()
                },
                currentFolder = Folder.getDefaultFolder(context),
                editType = editType,
                columnSpan = DEFAULT_COLUM_COUNT
        )
), GalleryController, PagingViewController<String> {

    companion object {
        private const val PAGE_SIZE = 66
        private const val FIRST_PAGE_NUMBER = 0
        private const val MAX_COLUM_COUNT = 4
        private const val MIN_COLUM_COUNT = 2

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
                        folder = state.currentFolder.value,
                )
                .schedulersIoToMain()
    }

    private val pagingViewControllerDelegate = PagingViewController.Delegate(
            getCurrentDataAction = { state.imageList },
            updateStateAction = { updateState { copy(imageListState = it) } },
            updateData = { updateState { copy(imageList = it) } }
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
            _singleEvent.onNext(RequestPermission)
        }
    }

    private fun initLoad() {
        loadFolders()
        loadImage()
    }

    private fun loadFolders() {
        galleryRepository.loadFolders()
                .map { folders -> folders.map { it.toFolder() } }
                .map { folders ->
                    val folderList = folders.toMutableList()

                    if (folders.isNotEmpty()) folderList.add(0, Folder.getDefaultFolder(context))

                    return@map folderList.toSet()
                }
                .lceSubscribe(
                        onEach = { folders -> updateState { copy(folders = folders) } },
                        onContent = { folders ->
                            val unusedFolders = folders.filter { it != state.currentFolder }.toSet()
                            updateState { copy(unusedFolders = unusedFolders) }
                        },
                        onError = this::onError
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
    override fun onRefreshError(exception: Throwable) = pagingViewControllerDelegate.onRefreshError(exception)
    override fun onRefresh() = pagingViewControllerDelegate.onRefresh()
    override fun onUpdateData(data: List<String>) = pagingViewControllerDelegate.onUpdateData(data)

    override fun onPermissionGranted() = initLoad()

    override fun onImageSelected(imageUri: String) = showTodo()

    override fun onLoadMoreImages() = paginator.loadNewPage()

    override fun onNavigateToSettingsClicked() = context.startAppSettingsActivity()

    override fun onBackClicked() = _navigationEvent.navigateUp()

    override fun onFolderChanged(folderName: String) {
        if (folderName == state.currentFolder.name) return

        val folder = state.folders.contentOrNull.orEmpty()
                .firstOrNull { it.name == folderName }
                ?: Folder.getDefaultFolder(context)

        val unusedFolders = state.folders.contentOrNull.orEmpty().filter { it != folder }.toSet()

        updateState { copy(currentFolder = folder, unusedFolders = unusedFolders) }
        paginator.pullToRefresh()
    }

    override fun onZoomIncreased() = updateState { copy(columnSpan = min(columnSpan + 1, MAX_COLUM_COUNT)) }

    override fun onZoomDecreased() = updateState { copy(columnSpan = max(columnSpan - 1, MIN_COLUM_COUNT)) }

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
