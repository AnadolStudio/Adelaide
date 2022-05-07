package com.anadolstudio.adelaide.view.screens.edit

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.anadolstudio.adelaide.domain.editphotoprocessor.EditProcessorContract
import com.anadolstudio.adelaide.domain.editphotoprocessor.EditProcessorIml
import com.anadolstudio.core.tasks.ProgressListener
import com.anadolstudio.core.tasks.Result
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.core.viewmodel.Communication
import java.io.File

class EditActivityViewModel : ViewModel() {

    private val editProcessor = EditProcessorIml()
    lateinit var viewController: EditViewController
        private set
    val originalBitmapCommunication = Communication.UiUpdate<Result<Bitmap>>()
    val currentBitmapCommunication = Communication.UiUpdate<Result<Bitmap>>()

    fun initEditProcessor(context: Context, path: String) {
        editProcessor.init(context, path)
            .onSuccess { originalBitmapCommunication.map(Result.Success(it)) }
            .onError { originalBitmapCommunication.map(Result.Error(it)) }
    }

    fun getEditProcessor(): EditProcessorContract = editProcessor

    fun processPreview() = editProcessor.processPreview()
        .onSuccess { currentBitmapCommunication.map(Result.Success(it)) }
        .onError { currentBitmapCommunication.map(Result.Error(it)) }

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun saveAsFile(
        context: Context,
        file: File,
        progressListener: ProgressListener<String>
    ): RxTask<String> = editProcessor.saveAsFile(context, file, progressListener)

    fun setEditViewController(viewController: EditViewController) {
        this.viewController = viewController
    }

}