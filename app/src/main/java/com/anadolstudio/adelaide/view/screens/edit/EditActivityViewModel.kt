package com.anadolstudio.adelaide.view.screens.edit

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.anadolstudio.core.tasks.ProgressListener
import com.anadolstudio.core.tasks.Result
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.core.viewmodel.Communication
import com.anadolstudio.photoeditorprocessor.processor.EditProcessorContract
import com.anadolstudio.photoeditorprocessor.processor.implementation.EditProcessorStudy
import java.io.File

class EditActivityViewModel : ViewModel() {

    private val editProcessor = EditProcessorStudy()
    val currentBitmapCommunication = Communication.UiUpdate<Result<Bitmap>>()
    lateinit var viewController: EditViewController
        private set

    fun initEditProcessor(context: Context, path: String): RxTask<Bitmap> {
        currentBitmapCommunication.map(Result.Loading())

        return editProcessor.init(context, path)
            .onSuccess { currentBitmapCommunication.map(Result.Success(it)) }
            .onError { currentBitmapCommunication.map(Result.Error(it)) }
    }

    fun getEditProcessor(): EditProcessorContract = editProcessor

    fun processPreview(support: Bitmap? = null): RxTask<Bitmap> {
        currentBitmapCommunication.map(Result.Loading())

        return editProcessor.processPreview(support)
            .onSuccess { currentBitmapCommunication.map(Result.Success(it)) }
            .onError { currentBitmapCommunication.map(Result.Error(it)) }
    }

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun saveAsFile(
        context: Context,
        file: File,
        progressListener: ProgressListener<String>
    ): RxTask<String> = editProcessor.saveAsFile(context, file, progressListener)

    fun setEditViewController(viewController: EditViewController) {
        this.viewController = viewController
    }

    override fun onCleared() {
        super.onCleared()
        editProcessor.clear()
    }

}