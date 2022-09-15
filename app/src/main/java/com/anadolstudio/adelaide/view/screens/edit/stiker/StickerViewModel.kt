package com.anadolstudio.adelaide.view.screens.edit.stiker

import android.content.Context
import androidx.lifecycle.ViewModel
import com.anadolstudio.adelaide.data.AssetData
import com.anadolstudio.adelaide.data.AssetsDirections
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.core.viewmodel.Communication

class StickerViewModel : ViewModel() {

    val adapterDataCommunication = Communication.UiUpdate<Result<MutableList<String>>>()

    fun loadAdapterData(context: Context) {
        adapterDataCommunication.map(Result.Loading())

        RxTask.Base.Quick {
            AssetData.getPathList(context, AssetsDirections.STICKER_DIR)
        }
                .onSuccess { adapterDataCommunication.map(Result.Success(it)) }
                .onError { ex -> adapterDataCommunication.map(Result.Error(ex)) }
    }

}
