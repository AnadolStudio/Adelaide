package com.anadolstudio.adelaide.view.screens.edit.effect

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.anadolstudio.adelaide.data.AssetData
import com.anadolstudio.adelaide.data.AssetsDirections
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.core.viewmodel.Communication
import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil

class EffectEditViewModel : ViewModel() {


    data class AdapterData(val thumbnail: Bitmap, val paths: MutableList<String>)

    val adapterDataCommunication = Communication.UiUpdate<Result<AdapterData>>()

    fun loadData(context: Context, bitmap: Bitmap) {
        adapterDataCommunication.map(Result.Loading())

        RxTask.Base.Quick {
            val crop = BitmapCommonUtil.centerCrop(bitmap)
            val thumbnail = BitmapCommonUtil.scaleBitmap(400F, 400F, crop, true)
            crop.recycle()

            val paths: MutableList<String> = mutableListOf<String>().apply {
                add("")
                addAll(AssetData.getPathList(context, AssetsDirections.EFFECTS_DIR))
            }

            AdapterData(thumbnail, paths)
        }
                .onSuccess { adapterDataCommunication.map(Result.Success(it)) }
                .onError { ex -> adapterDataCommunication.map(Result.Error(ex)) }
    }
}
