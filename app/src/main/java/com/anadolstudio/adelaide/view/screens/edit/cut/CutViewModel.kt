package com.anadolstudio.adelaide.view.screens.edit.cut

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import androidx.lifecycle.MutableLiveData
import com.anadolstudio.adelaide.data.AssetData
import com.anadolstudio.adelaide.data.AssetsDirections
import com.anadolstudio.adelaide.view.screens.edit.DrawingViewModel
import com.anadolstudio.core.livedata.onNext
import com.anadolstudio.core.livedata.toImmutable
import com.anadolstudio.core.rx_util.quickSingleFrom
import com.anadolstudio.core.rx_util.smartSubscribe
import com.anadolstudio.core.viewmodel.Communication
import com.anadolstudio.photoeditorprocessor.data.photo_segmenter.PhotoSegmenter
import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil

class CutViewModel : DrawingViewModel() {
    companion object {
        const val CUSTOM = "custom"
    }

    val _maskLiveData = MutableLiveData<CutViewState>()
    val maskLiveData = _maskLiveData.toImmutable()

    val adapterDataCommunication = Communication.UiUpdate<Result<MutableList<String>>>()

    private val service = PhotoSegmenter()

    fun createMask(bitmap: Bitmap, sizePoint: PointF) {
        _maskLiveData.onNext(CutViewState.Loading)

        service.init(
                bitmap = bitmap,
                onMaskReady = { result ->
                    when (result) {
                        is Result.Success -> {
                            val scaleMask = BitmapCommonUtil.scaleBitmap(sizePoint.x, sizePoint.y, result.data)
                            maskLiveData.map(Result.Success(scaleMask))
                        }
                        is Result.Error -> maskLiveData.map(Result.Error(result.error))
                        else -> {}
                    }
                }
        )
    }

    fun loadAdapterData(context: Context) {
        adapterDataCommunication.map(Result.Loading())

        quickSingleFrom {
            // TODO В репозиторий
            mutableListOf<String>().apply {
                add(CUSTOM)
                addAll(AssetData.getPathList(context, AssetsDirections.BACKGROUND_DIR))
            }
        }.smartSubscribe(
                onSuccess = { adapterDataCommunication.map(Result.Success(it)) },
                onError = { ex -> adapterDataCommunication.map(Result.Error(ex)) }
        )
    }

    override fun onCleared() {
        super.onCleared()

        val state = maskLiveData.value
        if (state is CutViewState.Content) state.mask.recycle()

    }


}
