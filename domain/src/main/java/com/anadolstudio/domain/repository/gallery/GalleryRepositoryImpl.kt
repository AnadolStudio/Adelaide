package com.anadolstudio.domain.repository.gallery

import android.Manifest
import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Images.Media
import android.provider.MediaStore.MediaColumns.MIME_TYPE
import android.provider.MediaStore.MediaColumns._ID
import androidx.annotation.RequiresPermission
import com.anadolstudio.core.common_extention.ifNotEmpty
import com.anadolstudio.core.common_extention.nullIfEmpty
import com.anadolstudio.core.rx_util.singleFrom
import com.anadolstudio.data.repository.GalleryRepository
import io.reactivex.Single
import java.io.File

class GalleryRepositoryImpl : GalleryRepository {

    private companion object {
        const val AND = " and "
        const val FORMAT = 1
        val legalFormat = listOf("jpg", "jpeg", "png", "webp")
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    override fun loadImages(
            context: Context,
            size: Int,
            folder: String?,
            lastItemIndex: Long?
    ): Single<List<String>> = singleFrom {
        mutableListOf<String>().also { images ->
            val uri: Uri = Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(_ID, MIME_TYPE, Media.BUCKET_DISPLAY_NAME)
            val selectionArg = mutableListOf<String>()

            var selection = folder?.also(selectionArg::add)
                    ?.let { "${Media.BUCKET_DISPLAY_NAME} = ?" }
                    .orEmpty()

            lastItemIndex?.toString()?.also { lastIndex ->
                selectionArg.add(lastIndex)
                selection = selection.ifNotEmpty { AND }.plus("$_ID < ?")
            }

            context.contentResolver.query(
                    uri, projection, selection.nullIfEmpty(), selectionArg.toTypedArray().nullIfEmpty(), "$_ID DESC"
            )?.use { cursor ->
                val columnDataIndex = cursor.getColumnIndexOrThrow(_ID)
                val mimeTypeIndex = cursor.getColumnIndexOrThrow(MIME_TYPE)
                var count = 0

                while (cursor.moveToNext() && count < size) {
                    val currentFormat = cursor.getString(mimeTypeIndex).split("/".toRegex()).toTypedArray()[FORMAT]
                    if (currentFormat !in legalFormat) continue

                    images.add(
                            Uri.withAppendedPath(uri, cursor.getString(columnDataIndex)).toString()
                    )

                    count++
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    override fun loadFolders(context: Context): Single<Set<String>> = singleFrom {
        HashSet<String>().also { folders ->
            val uri: Uri = Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(Media.BUCKET_DISPLAY_NAME)

            context.contentResolver.query(uri, projection, null, null, "$_ID DESC")
                    ?.use { cursor ->
                        val columnIndexData = cursor.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME)

                        while (cursor.moveToNext()) {
                            Uri.withAppendedPath(uri, cursor.getString(columnIndexData)).path?.also { path ->
                                folders.add(File(path).name)
                            }
                        }
                    }
        }
    }
}
