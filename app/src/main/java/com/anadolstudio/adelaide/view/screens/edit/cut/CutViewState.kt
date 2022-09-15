package com.anadolstudio.adelaide.view.screens.edit.cut

import android.graphics.Bitmap

sealed class CutViewState {

    object Loading : CutViewState()

    class Content(val mask: Bitmap) : CutViewState()

    class Error(val error: Throwable) : CutViewState()
}

