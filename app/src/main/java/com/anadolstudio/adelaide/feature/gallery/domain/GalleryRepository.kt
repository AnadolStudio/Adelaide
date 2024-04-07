package com.anadolstudio.adelaide.feature.gallery.domain

import com.anadolstudio.utils.data_source.media.Folder
import com.anadolstudio.utils.data_source.media.Image

interface GalleryRepository {

    suspend fun loadImages(pageSize: Int = 2_000, folder: String?): List<Image>

    suspend fun loadFolders(): Set<Folder>
}
