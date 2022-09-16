package com.anadolstudio.adelaide.view.screens.edit.effect

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MediatorLiveData
import com.anadolstudio.adelaide.data.AssetData
import com.anadolstudio.adelaide.data.AssetsDirections
import com.anadolstudio.core.livedata.onNext
import com.anadolstudio.core.livedata.toImmutable
import com.anadolstudio.core.rx_util.quickSingleFrom
import com.anadolstudio.core.rx_util.smartSubscribe
import com.anadolstudio.core.viewmodel.BaseViewModel
import com.anadolstudio.photoeditorcore.util.BitmapCommonUtil

class EffectEditViewModel : BaseViewModel() {

    private val _adapterData = MediatorLiveData<AdapterData>()
    val adapterData = _adapterData.toImmutable()

    fun loadData(context: Context, bitmap: Bitmap) {
        quickSingleFrom {
            val crop = BitmapCommonUtil.centerCrop(bitmap)
            val thumbnail = BitmapCommonUtil.scaleBitmap(400F, 400F, crop, true)
            crop.recycle()

            val paths: MutableList<String> = mutableListOf<String>().apply {
                add("")
                addAll(AssetData.getPathList(context, AssetsDirections.EFFECTS_DIR))
            }

            AdapterData(thumbnail, paths)
        }.smartSubscribe(
                onSuccess = _adapterData::onNext
        ).disposeOnViewModelDestroy()
    }

    data class AdapterData(val thumbnail: Bitmap, val paths: List<String>)

}
