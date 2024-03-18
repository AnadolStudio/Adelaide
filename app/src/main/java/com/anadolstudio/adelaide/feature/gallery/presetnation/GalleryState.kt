package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.adelaide.lce.Lce
import com.anadolstudio.utils.data_source.media.Folder

data class GalleryState(
        val columnSpan: Int,
        val imageState: ImageState = ImageState(),
        val folderState: FolderState = FolderState()
) {
    constructor(
            columnSpan: Int,
    ) : this(
            columnSpan = columnSpan,
            imageState = ImageState()
    )
}

data class FolderState(
    val folderVisible: Boolean = false,
    val foldersLce: Lce<Set<Folder>> = Lce.Loading,
    val folders: Set<Folder> = emptySet(),
    val currentFolder: Folder? = null,
)

data class ImageState(
        val imageList: List<String> = emptyList(),
)

