package com.anadolstudio.adelaide.feature.gallery.data

import androidx.paging.PagingData
import com.anadolstudio.adelaide.feature.gallery.domain.GalleryRepository
import com.anadolstudio.adelaide.util.pagingFlow
import com.anadolstudio.utils.data_source.media.Folder
import com.anadolstudio.utils.data_source.media.MediaDataStorage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(private val mediaDataStorage: MediaDataStorage) :
    GalleryRepository {

    override suspend fun loadImages(
        pageSize: Int,
        folder: String?
    ): Flow<PagingData<String>> =
        pagingFlow(pageSize) { pageIndex: Int, _: Int ->
            mediaDataStorage.loadImages(pageIndex = pageIndex, pageSize = pageSize, folder = folder)
        }

    override suspend fun loadFolders(): Set<Folder> = mediaDataStorage.loadFolders()

}
