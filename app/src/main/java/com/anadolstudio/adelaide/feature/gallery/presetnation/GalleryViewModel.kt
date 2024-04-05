package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.Manifest
import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.viewmodel.BaseContentViewModel
import com.anadolstudio.adelaide.base.viewmodel.navigateUp
import com.anadolstudio.adelaide.feature.gallery.domain.GalleryRepository
import com.anadolstudio.adelaide.lce.lceFlow
import com.anadolstudio.adelaide.lce.mapLceContent
import com.anadolstudio.adelaide.lce.onEachContent
import com.anadolstudio.utils.data_source.media.Folder
import com.anadolstudio.utils.util.extentions.hasAllPermissions
import com.anadolstudio.utils.util.extentions.startAppSettingsActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class GalleryViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val context: Context,
) : BaseContentViewModel<GalleryState>(
    GalleryState(
        columnSpan = DEFAULT_COLUM_COUNT,
    )
), GalleryController {

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

    init {
        checkPermissionAndLoad()
    }

    val galleryFlow = MutableStateFlow<PagingData<String>>(PagingData.empty())

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

    private fun loadFolders() {
        lceFlow { emit(galleryRepository.loadFolders()) }
            .mapLceContent { folders ->
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

                return@mapLceContent folderList.toSet()
            }
            .onEachContent { folders ->
                updateState { copy(folderState = folderState.copy(folders = folders)) }
            }
            .onEach { updateState { copy(folderState = folderState.copy(foldersLce = it)) } }
            .launchIn(viewModelScope)
    }

    private fun loadImage() = showTodo()

    override fun onPermissionGranted() = initLoad()

    override fun onImageSelected(imageUri: String) = showTodo()

    override fun onLoadMoreImages() = showTodo()

    override fun onNavigateToSettingsClicked() = context.startAppSettingsActivity()

    override fun onBackClicked() = _navigationEvent.navigateUp()

    override fun onFolderChanged(folder: Folder) {
        if (folder == state.folderState.currentFolder) return

        updateState { copy(folderState = folderState.copy(currentFolder = folder)) }
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
