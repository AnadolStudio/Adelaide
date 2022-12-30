package com.anadolstudio.adelaide.ui.screens.edit.main_edit_screen

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.MutableLiveData
import com.anadolstudio.core.livedata.toImmutable
import com.anadolstudio.core.viewmodel.BaseViewModel
import com.anadolstudio.photoeditorcore.domain.edit_processor.implementation.EditProcessorImpl
import com.anadolstudio.photoeditorcore.view.PhotoEditorView
import java.io.File

class EditActivityViewModel : BaseViewModel() {

    private val editProcessor = EditProcessorImpl()
    val editProcessorEvent = editProcessor.editProcessorEvent
    private val _currentBitmap = MutableLiveData<EditActivityViewState>()
    val currentBitmap = _currentBitmap.toImmutable()

    fun bindWithPhotoEditorView(photoEditorView: PhotoEditorView) {
        editProcessor.bindPhotoEditorView(photoEditorView)
    }

    fun initEditProcessor(context: Context, path: String) {
        editProcessor.loadImage(context, path)
    }

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun saveAsFile(context: Context, file: File) {
        editProcessor.savePhoto(context, file)
    }

    override fun onCleared() {
        super.onCleared()
        editProcessor.onCleared()
    }
}
