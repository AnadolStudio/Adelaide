package com.anadolstudio.adelaide.feature.start

import android.net.Uri
import com.anadolstudio.core.viewmodel.livedata.SingleCustomEvent

sealed class StartFragmentEvents : SingleCustomEvent() {

    class TakePhotoEvent(val uri: Uri?) : StartFragmentEvents()

}
