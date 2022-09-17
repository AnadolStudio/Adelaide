package com.anadolstudio.adelaide.view.screens.edit.main_edit_screen

import com.anadolstudio.core.livedata.SingleCustomEvent

sealed class EditActivityEvent : SingleCustomEvent() {

    class LoadingEvent(val isLoading: Boolean) : EditActivityEvent()

}
