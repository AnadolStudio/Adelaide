package com.anadolstudio.adelaide.view.screens.edit.cut

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import com.anadolstudio.adelaide.data.AssetData
import com.anadolstudio.adelaide.data.AssetsDirections
import com.anadolstudio.adelaide.view.screens.edit.DrawingViewModel
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
        mainBitmap: Bitmap,
        drawBitmap: Bitmap
    ): RxTask<Bitmap> = RxTask.Base.Quick {
        BitmapCutUtil.cutAndSet(context, mainBitmap, drawBitmap)
    }

    override fun onCleared() {
        super.onCleared()

        val result = mask.getValue()
        if (result is Result.Success) result.data.recycle()

    }
}