package com.anadolstudio.adelaide.feature.gallery.domain

import com.anadolstudio.utils.data_source.media.Folder
import com.anadolstudio.utils.data_source.media.Image
import io.reactivex.Single

interface GalleryRepository {

    fun loadImages(
            pageIndex: Int,
            pageSize: Int,
            folder: String?
    ): Single<List<Image>>

    fun loadFolders(): Single<Set<Folder>>
}
