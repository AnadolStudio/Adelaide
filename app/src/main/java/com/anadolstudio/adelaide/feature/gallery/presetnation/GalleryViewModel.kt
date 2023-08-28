package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anadolstudio.adelaide.base.viewmodel.BaseContentViewModel
import com.anadolstudio.adelaide.base.viewmodel.navigateUp
import com.anadolstudio.adelaide.di.DI
import com.anadolstudio.adelaide.feature.gallery.presetnation.model.Folder
import com.anadolstudio.adelaide.feature.gallery.presetnation.model.toFolder
import com.anadolstudio.core.common_extention.startAppSettingsActivity
import com.anadolstudio.core.rx_util.lceSubscribe
import com.anadolstudio.core.rx_util.schedulersIoToMain
import com.anadolstudio.core.util.paginator.PaginatorImpl
import com.anadolstudio.core.util.paginator.PagingViewController
import com.anadolstudio.data.repository.GalleryRepository
import io.reactivex.Single
import javax.inject.Inject

class GalleryViewModel(
        private val galleryRepository: GalleryRepository,
        private val context: Context,
) : BaseContentViewModel<GalleryState>(
        GalleryState(currentFolder = Folder.getDefaultFolder(context))
), GalleryController, PagingViewController<String> {

    private companion object {
        const val PAGE_SIZE = 66
        const val FIRST_PAGE_NUMBER = 0
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
        initLoad()
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

    override fun onImageSelected(imageUri: String) {
        // TODO("Not yet implemented")
    }

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

    @Suppress("UNCHECKED_CAST")
    class Factory : ViewModelProvider.NewInstanceFactory() {

        @Inject
        lateinit var context: Context

        @Inject
        lateinit var galleryRepository: GalleryRepository

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            DI.appComponent.inject(this)
            return GalleryViewModel(
                    galleryRepository = galleryRepository,
                    context = context
            ) as T
        }
    }
}
