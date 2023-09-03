package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.adelaide.feature.start.EditType
import com.anadolstudio.core.data_source.media.Folder
import com.anadolstudio.core.util.paginator.PagingDataState
import com.anadolstudio.core.viewmodel.LceState

data class GalleryState(
        val editType: EditType,
        val columnSpan: Int,
        val imageList: List<String> = emptyList(),
        val imageListState: PagingDataState<String> = PagingDataState.Empty(),
        val foldersLce: LceState<Set<Folder>> = LceState.Loading(),
        val folders: Set<Folder> = emptySet(),
        val currentFolder: Folder? = null,
        val isRefreshing: Boolean = false,
        val folderIsMoving: Boolean = false,
)

