package com.anadolstudio.adelaide.ui.screens.edit.main_edit_screen

import com.anadolstudio.core.livedata.SingleCustomEvent

sealed class EditActivityEvent : SingleCustomEvent() {

    class LoadingEvent(val isLoading: Boolean) : EditActivityEvent()

}
