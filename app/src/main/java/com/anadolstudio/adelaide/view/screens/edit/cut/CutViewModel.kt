package com.anadolstudio.adelaide.view.screens.edit.cut

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import com.anadolstudio.adelaide.data.AssetData
import com.anadolstudio.adelaide.data.AssetsDirections
import com.anadolstudio.adelaide.view.screens.edit.DrawingViewModel
import com.anadolstudio.core.tasks.ProgressListener
import com.anadolstudio.core.tasks.Result
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.core.viewmodel.Communication
import com.anadolstudio.photoeditorprocessor.data.segmenter.SegmenterService
import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil
import com.anadolstudio.photoeditorprocessor.util.BitmapCutUtil

class CutViewModel : DrawingViewModel() {
    companion object {
        const val CUSTOM = "custom"
    }

    val mask = Communication.UiUpdate<Result<Bitmap>>()
    val adapterDataCommunication = Communication.UiUpdate<Result<MutableList<String>>>()

    private val service = SegmenterService()

    fun createMask(context: Context, bitmap: Bitmap, sizePoint: PointF) {
        mask.map(Result.Loading())

        service.initWithProcess(bitmap, context) {
            when (it) {
                is Result.Success -> {
                    val scaleMask = BitmapCommonUtil.scaleBitmap(
                        sizePoint.x, sizePoint.y, it.data
                    )
                    mask.map(Result.Success(scaleMask))
                }
                is Result.Error -> mask.map(Result.Error(it.error))
                else -> {}
            }
        }
    }

    fun loadAdapterData(context: Context) {
        adapterDataCommunication.map(Result.Loading())

        RxTask.Base.Quick {
            mutableListOf<String>().apply {
                add(CUSTOM)
                addAll(AssetData.getPathList(context, AssetsDirections.BACKGROUND_DIR))
            }
        }
            .onSuccess { adapterDataCommunication.map(Result.Success(it)) }
            .onError { ex -> adapterDataCommunication.map(Result.Error(ex)) }
    }

    fun cutByMask(
        context: Context,
        processListener: ProgressListener<String>?,
        mainBitmap: Bitmap,
        drawBitmap: Bitmap
    ): RxTask<Bitmap> = RxTask.Progress.Quick(processListener) {
        processListener?.onProgress("Setup...")

        val bitmap = BitmapCommonUtil.scaleBitmap(mainBitmap, drawBitmap)

        val main = Point(mainBitmap.width, mainBitmap.height)
        val support = Point(bitmap.width, bitmap.height)

        val pixelsOriginal = IntArray(main.x * main.y)
        val pixelsInverseOriginal = IntArray(main.x * main.y)
        val pixelsBitmap = IntArray(support.x * support.y)

        processListener?.onProgress("Cutting...")

        if (pixelsBitmap.size != pixelsOriginal.size) throw IllegalArgumentException()

        mainBitmap.getPixels(pixelsOriginal, 0, main.x, 0, 0, main.x, main.y)
        bitmap.getPixels(pixelsBitmap, 0, support.x, 0, 0, support.x, support.y)
        val edgePixels = BitmapCutUtil.getEdgePixels(bitmap)

        for (i in pixelsOriginal.indices) {
            if (pixelsBitmap[i] == Color.TRANSPARENT) {
                pixelsInverseOriginal[i] = pixelsOriginal[i]
                pixelsOriginal[i] = Color.TRANSPARENT
            }
        }

        val edgePixelsArray = pixelsOriginal.clone()

        for (i in edgePixels.keys) {
            pixelsOriginal[i] = Color.TRANSPARENT
        }

        processListener?.onProgress("Blurring...")

        val result = BitmapCutUtil.blur(context, pixelsOriginal, edgePixelsArray, main.x, main.y)
        result.getPixels(pixelsOriginal, 0, main.x, 0, 0, main.x, main.y)

        val inverseBlur = BitmapCutUtil.blur(context, pixelsInverseOriginal, main.x, main.y)
        inverseBlur.getPixels(pixelsInverseOriginal, 0, main.x, 0, 0, main.x, main.y)

        processListener?.onProgress("Done")

        for (i in pixelsInverseOriginal.indices) {
            val alpha = 255 - Color.alpha(pixelsInverseOriginal[i])
            pixelsOriginal[i] = BitmapCutUtil.getColorWithAlpha(pixelsOriginal[i], alpha)
        }

        Bitmap.createBitmap(pixelsOriginal, main.x, main.y, Bitmap.Config.ARGB_8888)
    }

    override fun onCleared() {
        super.onCleared()

        val result = mask.getValue()
        if (result is Result.Success) result.data.recycle()

    }
}