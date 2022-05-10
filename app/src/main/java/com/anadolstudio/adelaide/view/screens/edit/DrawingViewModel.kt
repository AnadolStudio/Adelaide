package com.anadolstudio.adelaide.view.screens.edit

import android.graphics.Color
import androidx.lifecycle.ViewModel
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder

open class DrawingViewModel : ViewModel() {

    companion object {
        internal const val XSMALL = 5.0f
        internal const val XLARGE = 100.0f
        internal const val NORMAL = (XLARGE - XSMALL) / 2
    }

    data class Settings(
        var isBrush: Boolean = true,
        var size: Float = NORMAL,
        var color: Int = Color.WHITE
    )

    val settings = Settings()

    open fun setupBrush(photoEditor: PhotoEditor,size: Float = settings.size) {
        settings.size = size
        photoEditor.setBrushDrawingMode(true)

        val builder = ShapeBuilder()
            .withShapeColor(settings.color)
            .withShapeSize(settings.size)

        photoEditor.setShape(builder)

        if (!settings.isBrush) photoEditor.brushEraser()
    }
}