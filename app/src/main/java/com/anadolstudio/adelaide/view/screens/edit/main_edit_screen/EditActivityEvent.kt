package com.anadolstudio.adelaide.view.screens.edit.main_edit_screen

import com.anadolstudio.core.livedata.SingleCustomEvent

sealed class EditActivityEvent : SingleCustomEvent() {

    object CantOpenPhotoEvent : EditActivityEvent()

    class LoadingEvent(val isLoading: Boolean) : EditActivityEvent()

    class SuccessSaveEvent(val path: String) : EditActivityEvent()
}
