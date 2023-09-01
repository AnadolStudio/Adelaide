package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.adelaide.feature.gallery.presetnation.model.Folder
import com.anadolstudio.adelaide.feature.start.EditType
import com.anadolstudio.core.util.paginator.PagingDataState
import com.anadolstudio.core.viewmodel.LceState

data class GalleryState(
        val imageList: List<String> = emptyList(),
        val imageListState: PagingDataState<String> = PagingDataState.Empty(),
        val folders: LceState<Set<Folder>> = LceState.Loading(),
        val unusedFolders: Set<Folder> = emptySet(),
        val currentFolder: Folder,
        val editType: EditType,
        val columnSpan: Int,
)

