package com.anadolstudio.adelaide.view.screens.edit.cut

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.anadolstudio.adelaide.data.AssetData
import com.anadolstudio.adelaide.data.AssetsDirections
import com.anadolstudio.adelaide.view.screens.edit.DrawingViewModel
import com.anadolstudio.core.livedata.onNext
import com.anadolstudio.core.livedata.toImmutable
import com.anadolstudio.core.rx_util.quickSingleFrom
import com.anadolstudio.core.rx_util.singleFrom
import com.anadolstudio.core.rx_util.smartSubscribe
import com.anadolstudio.domain.usecase.cut_background.CutBackgroundUseCase
import com.anadolstudio.photoeditorcore.domain.photo_segmenter.PhotoSegmenter
import com.anadolstudio.photoeditorcore.domain.util.BitmapCommonUtil

class CutViewModel : DrawingViewModel() {
    companion object {
        const val CUSTOM = "custom"
    }

    private val _maskLiveData = MutableLiveData<CutViewState>()
    val maskLiveData = _maskLiveData.toImmutable()
    private val cutBackgroundUseCase = CutBackgroundUseCase()

    private val _adapterData = MediatorLiveData<List<String>>()
    val adapterData = _adapterData.toImmutable()

    private val service = PhotoSegmenter()

    fun createMask(bitmap: Bitmap, sizePoint: PointF, colorMask: Int) {
        _maskLiveData.onNext(CutViewState.Loading)

        service.init(
                bitmap = bitmap,
                onMaskReady = { result ->
                    singleFrom {
                        val mask = cutBackgroundUseCase.process(colorMask, result)

                        return@singleFrom BitmapCommonUtil.scaleBitmap(sizePoint.x, sizePoint.y, mask)
                    }.smartSubscribe(
                            onSuccess = { mask ->
                                _maskLiveData.onNext(CutViewState.Content(mask))
                            },
                            onError = { error ->
                                _maskLiveData.onNext(CutViewState.Error(error))
                            }
                    ).disposeOnViewModelDestroy()
                }
        )
    }

    fun getMask(): Bitmap? = (_maskLiveData.value as? CutViewState.Content)?.mask

    fun loadAdapterData(context: Context) {
        quickSingleFrom {
            // TODO В репозиторий
            mutableListOf<String>().apply {
                add(CUSTOM)
                addAll(AssetData.getPathList(context, AssetsDirections.BACKGROUND_DIR))
            }
        }.smartSubscribe(
                onSuccess = _adapterData::onNext,
        )
    }

    override fun onCleared() {
        super.onCleared()

        val state = maskLiveData.value
        if (state is CutViewState.Content) state.mask.recycle()
    }

    fun cutByMask(context: Context, mainBitmap: Bitmap, drawBitmap: Bitmap) {
        quickSingleFrom {
            cutBackgroundUseCase.cutByMask(
                    context = context,
                    processListener = null,
                    mainBitmap = mainBitmap,
                    drawBitmap = drawBitmap
            )
        }.smartSubscribe(
                onSuccess = { cutBitmap -> _singleEvent.onNext(MaskWasCutEvent(cutBitmap)) },
        )
    }
}
