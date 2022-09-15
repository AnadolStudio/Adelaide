package com.anadolstudio.adelaide.view.screens.edit.stiker

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.anadolstudio.adelaide.data.AssetData
import com.anadolstudio.adelaide.data.AssetsDirections
import com.anadolstudio.core.livedata.onNext
import com.anadolstudio.core.livedata.toImmutable
import com.anadolstudio.core.rx_util.quickSingleFrom
import com.anadolstudio.core.rx_util.smartSubscribe
import com.anadolstudio.core.viewmodel.BaseViewModel

class StickerViewModel : BaseViewModel() {

    private val _adapterData = MediatorLiveData<List<String>>()
    val adapterData = _adapterData.toImmutable()

    fun loadAdapterData(context: Context) {
        quickSingleFrom {
            AssetData.getPathList(context, AssetsDirections.STICKER_DIR)
        }.smartSubscribe(
                onSuccess = _adapterData::onNext
        ).disposeOnViewModelDestroy()
    }
}
