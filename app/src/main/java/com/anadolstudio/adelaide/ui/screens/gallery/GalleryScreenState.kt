package com.anadolstudio.adelaide.ui.screens.gallery

sealed class GalleryScreenState {

    class Content(
            val images: List<String>,
            val folders: Set<String>? = null,
            val isLoadMore: Boolean = false
    ) : GalleryScreenState()

    class Error(val error: Throwable) : GalleryScreenState()

    object Loading : GalleryScreenState()

    object Empty : GalleryScreenState()

}
