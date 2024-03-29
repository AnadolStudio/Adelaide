package com.anadolstudio.domain.repository.gallery

import com.anadolstudio.core.data_source.MediaDataStorage
import com.anadolstudio.data.repository.GalleryRepository
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

    override fun loadFolders(): Single<Set<String>> = mediaDataStorage.loadFolders()

}
