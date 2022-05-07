package com.anadolstudio.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import java.lang.IllegalArgumentException

interface BitmapDecoder {

    fun decode(path: String, reqWidth: Int, reqHeight: Int): Bitmap

    abstract class Abstract : BitmapDecoder {

        open fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int,
            isHard: Boolean = true
        ): Int {
            // Реальные размеры изображения
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2

                // Вычисляем наибольший inSampleSize, который будет кратным двум
                // и оставит полученные размеры больше, чем требуемые
                val inProgress =
                    if (isHard)
                        halfHeight / inSampleSize > reqHeight || halfWidth / inSampleSize > reqWidth
                    else
                        halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth

                while (inProgress) inSampleSize *= 2

            }
            return inSampleSize
        }

        protected fun getDegree(orientation: Int): Int = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        protected fun rotate(source: Bitmap, degree: Float): Bitmap {
            if (degree == 0f) return source

            val matrix = Matrix().apply {
                postRotate(degree)
            }

            return Bitmap.createBitmap(
                source, 0, 0,
                source.width, source.height,
                matrix, true
            )
        }
    }

    class FromRealPath : Abstract() {

        override fun decode(path: String, reqWidth: Int, reqHeight: Int): Bitmap {

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            //RealPath
            BitmapFactory.decodeFile(path, options)
            val orientation = ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, true)
            options.inJustDecodeBounds = false
            val bitmap: Bitmap = BitmapFactory.decodeFile(path, options)
            val degree: Int = getDegree(orientation)

            return rotate(bitmap, degree.toFloat())
        }

    }

    class FromContentPath(val context: Context) : Abstract() {

        override fun decode(path: String, reqWidth: Int, reqHeight: Int): Bitmap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            // Content
            val pfd = context.contentResolver.openFileDescriptor(Uri.parse(path), "r")
                ?: throw IllegalArgumentException("ParcelFileDescriptor is null")

            BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor, null, options)

            val orientation = ExifInterface(pfd.fileDescriptor).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 1
            )

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, true)
            options.inJustDecodeBounds = false

            val bitmap = pfd.let {
                BitmapFactory.decodeFileDescriptor(
                    pfd.fileDescriptor,
                    null,
                    options
                )
            }

            val degree: Int = getDegree(orientation)

            return rotate(bitmap, degree.toFloat())
        }

    }

}