package com.anadolstudio.data.repository

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import io.reactivex.Single

interface GalleryRepository {

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun loadImages(
            context: Context,
            size: Int,
            folder: String? = null,
            lastItemIndex: Long? = null
    ): Single<List<String>>

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun loadFolders(context: Context): Single<Set<String>>
}
