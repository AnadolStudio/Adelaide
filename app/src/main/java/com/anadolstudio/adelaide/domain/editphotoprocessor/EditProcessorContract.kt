package com.anadolstudio.adelaide.domain.editphotoprocessor

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RequiresPermission
import com.anadolstudio.adelaide.view.screens.edit.enumeration.MainFunctions
import com.anadolstudio.core.tasks.ProgressListener
import com.anadolstudio.core.tasks.RxTask
import java.io.File

interface EditProcessorContract {

    val containerFunctions: LinkedHashMap<String, EditFunction>

    val applyFuncList: MutableSet<EditFunction>

    fun init(context: Context, path: String): RxTask<Bitmap>

    fun process(bitmap: Bitmap, func: EditFunction): Bitmap

    fun processAll(bitmap: Bitmap): Bitmap

    fun getOriginalImage(): Bitmap

    fun getCurrentImage(): Bitmap?// Большая сторона <= 1280

    fun addFunction(func: EditFunction)

    fun getFunction(type: MainFunctions): EditFunction?


    abstract class Base : EditProcessorContract {

        abstract fun processPreview(): RxTask<Bitmap>

        abstract fun saveAsBitmap(): RxTask<Bitmap>

        @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
        abstract fun saveAsFile(
            context: Context, file: File, processListener: ProgressListener<String>
        ): RxTask<String>

        abstract fun clear()

    }
}