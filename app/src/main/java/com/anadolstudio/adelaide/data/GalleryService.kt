package com.anadolstudio.adelaide.data

import android.Manifest
import android.net.Uri
import android.provider.MediaStore.Images.Media
import android.provider.MediaStore.MediaColumns.MIME_TYPE
import android.provider.MediaStore.MediaColumns._ID
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.core.tasks.RxTask
import java.io.File

typealias GalleryListener = (folders: Set<String>, images: List<String>) -> Unit

class GalleryService {

    private var images = mutableListOf<String>()
    private var folders = mutableSetOf<String>()

    private val listeners = mutableSetOf<GalleryListener>()

    companion object {
        private const val TAG = "GalleryService"
        private const val ONE_PORTION = 99
        private const val NULL = -1L
        private val legalFormat = listOf("jpg", "jpeg", "png", "webp")
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun loadImages(
        activity: AppCompatActivity,
        folder: String? = null,
        lastItemId: Long = NULL
    ): RxTask<Unit> = RxTask.Base {
        val images = mutableListOf<String>()
        val uri: Uri = Media.EXTERNAL_CONTENT_URI

        Log.d(TAG, "loadImages: uri $uri")

        val projection = arrayOf(_ID, MIME_TYPE, Media.BUCKET_DISPLAY_NAME)
        var selection: String? = null
        var selectionArg: MutableList<String>? = null

        //TODO Можно написать более красиво?
        folder?.also {
            selection = "${Media.BUCKET_DISPLAY_NAME} = ?"
            selectionArg = mutableListOf(it)
        }

        if (lastItemId > NULL) {
            selection = if (selection != null) "$selection and $_ID < ?" else "$_ID < ?"
            if (selectionArg == null) selectionArg = ArrayList()
            selectionArg?.add(lastItemId.toString())
        }

        var selectionArgArray: Array<String>? = null
        selectionArg?.also { selectionArgArray = it.toTypedArray() }

        val cursor = activity.contentResolver.query(
            uri, projection, selection, selectionArgArray, "$_ID DESC"
        )

        if (cursor != null) {
            val columnIndexData = cursor.getColumnIndexOrThrow(_ID)
            val indexMT = cursor.getColumnIndexOrThrow(MIME_TYPE)
            var count = 0
            while (cursor.moveToNext() && count < ONE_PORTION) {
                val pathOfImage =
                    Uri.withAppendedPath(uri, cursor.getString(columnIndexData)).toString()

                //image/jpeg
                val format = cursor.getString(indexMT).split("/".toRegex()).toTypedArray()[1]

                if (!legalFormat.contains(format)) continue

                images.add(pathOfImage)
                count++
            }
            cursor.close()
        }
        this.images = images
        notifyChanges()
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun loadFolders(activity: AppCompatActivity): RxTask<Unit> = RxTask.Base {
        val folders: MutableSet<String> = HashSet()
        val uri: Uri = Media.EXTERNAL_CONTENT_URI

        Log.d(TAG, "getAllImagesDataObservable: uri $uri")

        val projection = arrayOf(Media.BUCKET_DISPLAY_NAME)

        val cursor = activity.contentResolver.query(
            uri, projection, null, null, "$_ID DESC"
        )

        if (cursor != null) {
            val columnIndexData = cursor.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val folderItem =
                    Uri.withAppendedPath(uri, cursor.getString(columnIndexData)).toString()
                folders.add(File(Uri.parse(folderItem).path!!).name)
            }
            cursor.close()
        }
        this.folders = folders
        notifyChanges()
    }

    fun addListener(listener: GalleryListener) {
        listeners.add(listener)
        if (folders.isNotEmpty())
            listener.invoke(folders, images)
    }

    fun removeListener(listener: GalleryListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        if (folders.isEmpty()) return

        listeners.forEach { it.invoke(folders, images) }
    }
}