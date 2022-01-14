package com.anadolstudio.adelaide.editphotoprocessor

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RequiresPermission
import java.io.File

interface EditProcessorContract {

    val containerFunctions: LinkedHashMap<String, EditFunction>

    val applyFuncList: MutableSet<EditFunction>

    fun setImage(path: String)

    fun saveAsBitmap(listener: EditListener<Bitmap>)

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun saveAsFile(context: Context, file: File, listener: EditListener<String>)

    fun processPreview()

    fun process(bitmap: Bitmap, func: EditFunction): Bitmap

    fun processAll(bitmap: Bitmap): Bitmap

    fun getOriginalImage(): Bitmap? // Большая сторона <= 1280

    fun getCurrentImage(): Bitmap?

    fun clear()
}