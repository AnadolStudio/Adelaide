package com.anadolstudio.adelaide.feature.gallery.presetnation

import com.anadolstudio.adelaide.feature.gallery.presetnation.model.Folder
import com.anadolstudio.core.presentation.ContentableState
import com.anadolstudio.core.util.paginator.PagingDataState
import com.anadolstudio.core.viewmodel.LceState

data class GalleryState(
        val imageList: List<String> = emptyList(),
        val imageListState: PagingDataState<String> = PagingDataState.Loading(),
        val folders: LceState<Set<Folder>> = LceState.Loading(),
        val unusedFolders: Set<Folder> = emptySet(),
        val currentFolder: Folder,
) : ContentableState {

    override val isShimmers: Boolean get() = imageListState.isShimmers
    override val isFullScreenLoading: Boolean get() = imageListState.isFullScreenLoading
    override val isEmpty: Boolean get() = imageListState.isEmpty
    override val isError: Boolean get() = imageListState.isError
    override val isContent: Boolean get() = imageListState.isContent
    override val getError: Throwable? get() = imageListState.getError
}

