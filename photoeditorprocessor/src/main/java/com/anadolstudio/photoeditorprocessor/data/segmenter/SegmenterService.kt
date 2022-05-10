package com.anadolstudio.photoeditorprocessor.data.segmenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.anadolstudio.core.tasks.Result
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.photoeditorprocessor.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import java.nio.ByteBuffer

class SegmenterService {

    companion object {
        private const val CONFIDENCE = 0.35f
    }

    fun initWithProcess(
        bitmap: Bitmap, context: Context, callback:
        ReadyCallback<Result<Bitmap>>
    ) {
        val options = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .build()

        val image = InputImage.fromBitmap(bitmap, 0)
        Segmentation.getClient(options).process(image)
            .addOnSuccessListener { mask ->

                process(context, mask)
                    .onSuccess { b -> callback(Result.Success(b)) }
                    .onError { callback(Result.Error(it)) }
            }
            .addOnFailureListener { callback(Result.Error(EmptyMaskException())) }
    }

    private fun process(
        context: Context, mask: SegmentationMask
    ): RxTask<Bitmap> = RxTask.Base.Quick {
        val color = ContextCompat.getColor(context, R.color.colorMask)

        val maskBuffer: ByteBuffer = mask.buffer
        val width: Int = mask.width
        val height: Int = mask.height

        val size = width * height
        val bits = IntArray(size)

        var count = 0f

        for (index in 0 until height * width) {
            val foregroundConfidence = maskBuffer.float
            if (foregroundConfidence > CONFIDENCE) {
                bits[index] = color
                count++
                continue
            }
        }

        if (count / size < 0.1f) throw SmallMaskException()

        Bitmap.createBitmap(bits, width, height, ARGB_8888)
    }
}