package com.anadolstudio.adelaide.ui.screens.edit.main_edit_screen

import android.graphics.Bitmap

sealed class EditActivityViewState {

    class Content(val bitmap: Bitmap) : EditActivityViewState()

    class Error(val error: Throwable) : EditActivityViewState()
}
