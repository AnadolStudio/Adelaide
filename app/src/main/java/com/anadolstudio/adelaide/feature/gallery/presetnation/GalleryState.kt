package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.paginator.PagingDataState
import com.anadolstudio.ui.viewmodel.states.ProgressState
import com.anadolstudio.utils.data_source.media.Folder

data class GalleryState(
        val columnSpan: Int,
        val imageState: ImageState = ImageState(),
        val folderState: FolderState = FolderState()
) {
    constructor(
            columnSpan: Int,
            pagingDataState: PagingDataState<String>
    ) : this(
            columnSpan = columnSpan,
            imageState = ImageState(pagingDataState = pagingDataState)
    )
}

data class FolderState(
        val folderVisible: Boolean = false,
        val progressState: ProgressState = ProgressState.Loading,
        val folders: Set<Folder> = emptySet(),
        val currentFolder: Folder? = null,
)

data class ImageState(
        val imageList: List<String> = emptyList(),
        val pagingDataState: PagingDataState<String> = PagingDataState.Empty(),
)

