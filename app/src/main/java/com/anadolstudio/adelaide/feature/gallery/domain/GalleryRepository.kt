package com.anadolstudio.adelaide.feature.gallery.domain

import androidx.paging.PagingData
import com.anadolstudio.utils.data_source.media.Folder
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {

    suspend fun loadImages(pageSize: Int, folder: String?): Flow<PagingData<String>>

    suspend fun loadFolders(): Set<Folder>
}
