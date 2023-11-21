package com.anadolstudio.adelaide.feature.gallery.data

import com.anadolstudio.adelaide.feature.gallery.domain.GalleryRepository
import com.anadolstudio.core.data_source.media.Folder
import com.anadolstudio.core.data_source.media.MediaDataStorage
import com.anadolstudio.core.util.rx.singleFrom
import io.reactivex.Single
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(private val mediaDataStorage: MediaDataStorage) : GalleryRepository {

    override fun loadImages(
            pageIndex: Int,
            pageSize: Int,
            folder: String?
    ): Single<List<String>> = singleFrom {
        mediaDataStorage.loadImages(pageIndex = pageIndex, pageSize = pageSize, folder = folder)
    }

    override fun loadFolders(): Single<Set<Folder>> = singleFrom {
        mediaDataStorage.loadFolders()
    }

}
