package com.anadolstudio.adelaide.feature.gallery.domain

import com.anadolstudio.utils.data_source.media.Folder
import io.reactivex.Single

interface GalleryRepository {

    fun loadImages(
            pageIndex: Int,
            pageSize: Int,
            folder: String?
    ): Single<List<String>>

    fun loadFolders(): Single<Set<Folder>>
}
