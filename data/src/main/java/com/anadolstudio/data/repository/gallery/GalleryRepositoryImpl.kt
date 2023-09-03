package com.anadolstudio.data.repository.gallery

import com.anadolstudio.core.data_source.media.Folder
import com.anadolstudio.core.data_source.media.MediaDataStorage
import com.anadolstudio.domain.repository.GalleryRepository
import io.reactivex.Single
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(private val mediaDataStorage: MediaDataStorage) : GalleryRepository {

    override fun loadImages(
            pageIndex: Int,
            pageSize: Int,
            folder: String?
    ): Single<List<String>> = mediaDataStorage.loadImages(
            pageIndex = pageIndex,
            pageSize = pageSize,
            folder = folder,
    )

    override fun loadFolders(): Single<Set<Folder>> = mediaDataStorage.loadFolders()

}
