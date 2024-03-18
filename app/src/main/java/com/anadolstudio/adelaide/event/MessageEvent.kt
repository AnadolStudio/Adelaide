package com.anadolstudio.adelaide.event

import androidx.annotation.StringRes
import com.anadolstudio.compose.ui.view.snackbar.SnackbarStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

internal data class MessageEvent(val message: Text, val snackbarStyle: SnackbarStyle) : Event
internal data class InterstitialMessageEvent(val message: Text, val snackbarStyle: SnackbarStyle) :
    Event
internal data class ErrorMessageEvent(val message: Text) : Event

internal fun EventsDispatcher.showMessage(
    message: String,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    offerEvent(MessageEvent(Text.Plain(message), snackbarStyle))
}

internal fun EventsDispatcher.showMessage(
    text: Text,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    offerEvent(MessageEvent(text, snackbarStyle))
}

internal fun MutableSharedFlow<Event>.showInterstitialMessage(
    text: Text,
    coroutineScope: CoroutineScope,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    coroutineScope.launch { emit(InterstitialMessageEvent(text, snackbarStyle)) }
}

internal fun MutableSharedFlow<Event>.showInterstitialMessage(
    message: String,
    coroutineScope: CoroutineScope,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    coroutineScope.launch { emit(InterstitialMessageEvent(Text.Plain(message), snackbarStyle)) }
}

internal fun MutableSharedFlow<Event>.showInterstitialMessage(
    messageRes: Int,
    coroutineScope: CoroutineScope,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    coroutineScope.launch { emit(InterstitialMessageEvent(Text.Resource(messageRes), snackbarStyle)) }
}

internal fun EventsDispatcher.showMessage(
    @StringRes resourceId: Int,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    offerEvent(MessageEvent(Text.Resource(resourceId), snackbarStyle))
}

internal fun EventsDispatcher.showError(message: String) {
    offerEvent(ErrorMessageEvent(Text.Plain(message)))
}

internal fun EventsDispatcher.showError(@StringRes resourceId: Int) {
    offerEvent(ErrorMessageEvent(Text.Resource(resourceId)))
}

internal fun EventsDispatcher.showError(error: Throwable?) {
    offerEvent(ErrorMessageEvent(Text.Plain(error?.message.orEmpty())))
}

private val todoMessages = listOf(
    "Извините, этот функционал пока не реализован \uD83D\uDE43",
    "Мы работаем над этим функционалом, следите за обновлениями \uD83D\uDC40",
    "Эта функция будет доступна в ближайшее время \uD83C\uDFC3",
)

/** Use it if functionality is not implemented yet. */
internal fun EventsDispatcher.showTodo() {
    offerEvent(MessageEvent(Text.Plain(todoMessages.random()), SnackbarStyle.Default))
}
