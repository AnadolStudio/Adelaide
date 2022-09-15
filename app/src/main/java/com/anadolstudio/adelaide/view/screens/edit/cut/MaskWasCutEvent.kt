package com.anadolstudio.adelaide.view.screens.edit.cut

import android.graphics.Bitmap
import com.anadolstudio.core.livedata.SingleCustomEvent

class MaskWasCutEvent(val cutBitmap: Bitmap) : SingleCustomEvent()
