package com.anadolstudio.adelaide.feature.gallery.data

import com.anadolstudio.adelaide.feature.gallery.domain.GalleryRepository
import com.anadolstudio.utils.data_source.media.Folder
import com.anadolstudio.utils.data_source.media.Image
import com.anadolstudio.utils.data_source.media.MediaDataStorage
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(private val mediaDataStorage: MediaDataStorage) :
    GalleryRepository {

    override suspend fun loadImages(
        pageSize: Int,
        folder: String?
    ): List<Image> =
        mediaDataStorage.loadImages(pageIndex = 0, pageSize = pageSize, folder = folder)

    override suspend fun loadFolders(): Set<Folder> = mediaDataStorage.loadFolders()

}
