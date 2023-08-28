package com.anadolstudio.data.repository

import io.reactivex.Single

interface GalleryRepository {

    fun loadImages(
            pageIndex: Int,
            pageSize: Int,
            folder: String?
    ): Single<List<String>>

    fun loadFolders(): Single<Set<String>>
}
