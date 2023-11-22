package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.adelaide.feature.start.EditType
import com.anadolstudio.core.data_source.media.Folder
import com.anadolstudio.core.util.paginator.PagingDataState
import com.anadolstudio.core.viewmodel.LceState

data class GalleryState(
        val editType: EditType,
        val columnSpan: Int,
        val imageState: ImageState = ImageState(),
        val folderState: FolderState = FolderState()
) {
    constructor(
            editType: EditType,
            columnSpan: Int,
            pagingDataState: PagingDataState<String>
    ) : this(
            editType = editType,
            columnSpan = columnSpan,
            imageState = ImageState(pagingDataState = pagingDataState)
    )
}

data class FolderState(
        val folderVisible: Boolean = false,
        val foldersLce: LceState<Set<Folder>> = LceState.Loading(),
        val folders: Set<Folder> = emptySet(),
        val currentFolder: Folder? = null,
)

data class ImageState(
        val imageList: List<String> = emptyList(),
        val pagingDataState: PagingDataState<String> = PagingDataState.Empty(),
)

