package com.anadolstudio.adelaide.domain.editphotoprocessor.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.anadolstudio.adelaide.R
import com.anadolstudio.core.tasks.ProgressListener
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

interface BitmapSaver {

    fun getUri(resolver: ContentResolver, context: Context, file: File): Uri?

    fun compress(resolver: ContentResolver, bitmap: Bitmap, uri: Uri?, file: File): String

    fun scanFile(context: Context, path: String)

    abstract class Abstract(private val progressListener: ProgressListener<String>?) : BitmapSaver {

        fun save(
            context: Context,
            bitmap: Bitmap,
            file: File
        ): String {
            var uri: Uri? = null
            val resolver = context.contentResolver

            try {
                progressListener?.onProgress("Get uri...")// TODO Через getString()
                uri = getUri(resolver, context, file)

                progressListener?.onProgress("Compress...")
                val path = compress(resolver, bitmap, uri, file)

                progressListener?.onProgress("Done")
                scanFile(context, path)

                return path

            } catch (e: FileNotFoundException) {
                uri?.let { resolver.delete(uri, null, null) }
            }

            throw IllegalArgumentException("Bitmap is not save")
        }

        override fun scanFile(context: Context, path: String) {
            MediaScannerConnection.scanFile(
                context,
                arrayOf(path),
                null
            ) { _: String?, _: Uri? -> Log.d("BitmapSaver", "onSuccess") }
        }
    }

    class BelowQ(progressListener: ProgressListener<String>?) : Abstract(progressListener) {
        override fun getUri(resolver: ContentResolver, context: Context, file: File): Uri? = null

        override fun compress(
            resolver: ContentResolver,
            bitmap: Bitmap,
            uri: Uri?,
            file: File
        ): String {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            return file.path
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    class AboveQ(progressListener: ProgressListener<String>?) : Abstract(progressListener) {

        override fun getUri(resolver: ContentResolver, context: Context, file: File): Uri {
            val relativePath =
                Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) // save directory

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                put(MediaStore.MediaColumns.MIME_TYPE, BitmapUtil.MIME_TYPE)
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            }

            val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            return resolver.insert(contentUri, contentValues)
                ?: throw Exception("Failed to create new  MediaStore record.")
        }

        override fun compress(
            resolver: ContentResolver,
            bitmap: Bitmap,
            uri: Uri?,
            file: File
        ): String {
            if (uri == null) throw IllegalArgumentException("Uri is null")

            resolver.openOutputStream(uri)?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            return uri.toString()
        }
    }

    object Factory {

        fun save(
            progressListener: ProgressListener<String>?,
            context: Context,
            bitmap: Bitmap,
            file: File
        ): String {
            val saver = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AboveQ(progressListener)
            } else {
                BelowQ(progressListener)
            }

            return saver.save(context, bitmap, file)
        }
    }
}