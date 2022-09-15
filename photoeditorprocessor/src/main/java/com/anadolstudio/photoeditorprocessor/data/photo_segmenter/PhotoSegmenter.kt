package com.anadolstudio.photoeditorprocessor.data.photo_segmenter

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import java.nio.ByteBuffer

class PhotoSegmenter {

    fun init(
            bitmap: Bitmap,
            onMaskReady: (PhotoSegmenterData) -> Unit,
            onFailure: ((Throwable) -> Unit)? = null
    ) {
        val options = SelfieSegmenterOptions.Builder()
                .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
                .build()

        val image = InputImage.fromBitmap(bitmap, 0)

        Segmentation.getClient(options).process(image)
                .addOnSuccessListener { segmentationMask ->

                    val data = PhotoSegmenterData(
                            byteBuffer = segmentationMask.buffer,
                            width = segmentationMask.width,
                            height = segmentationMask.height
                    )

                    onMaskReady.invoke(data)
                }
                .addOnFailureListener { onFailure?.invoke((EmptyMaskException())) }
    }

    data class PhotoSegmenterData(
            val byteBuffer: ByteBuffer,
            val width: Int,
            val height: Int
    )
}
