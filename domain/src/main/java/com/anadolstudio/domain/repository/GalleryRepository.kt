package com.anadolstudio.domain.repository

import com.anadolstudio.core.data_source.media.Folder
import io.reactivex.Single

interface GalleryRepository {

    fun loadImages(
            pageIndex: Int,
            pageSize: Int,
            folder: String?
    ): Single<List<String>>

    fun loadFolders(): Single<Set<Folder>>
}
