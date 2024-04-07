package com.anadolstudio.adelaide.event

import android.content.res.Resources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.anadolstudio.adelaide.feature.main.Navigator
import com.anadolstudio.compose.ui.view.snackbar.SnackbarHostState
import com.anadolstudio.compose.ui.view.snackbar.SnackbarStyle
import com.anadolstudio.compose.ui.view.text.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class EventQueue {

    private val eventsFlow = MutableStateFlow<List<Event>>(emptyList())

    /** Returns flow of events. */
    @OptIn(FlowPreview::class)
    val flow: Flow<Event>
        get() = eventsFlow.flatMapConcat { consumeAll() }

    /** Adds given [event] to the queue. */
    fun offerEvent(event: Event) {
        eventsFlow.update { it + event }
    }

    private fun consumeAll(): Flow<Event> = eventsFlow.getAndUpdate { emptyList() }.asFlow()
}

@Composable
internal fun ObserveEvents(
    events: EventQueue,
    snackbarHostState: SnackbarHostState,
    navigator: Navigator,
    resources: Resources = LocalContext.current.resources,
    lifecycleOwner: LifecycleOwner? = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    onEvent: (Event) -> Boolean = { false },
) {
    ObserveEvents(
        events = events,
        lifecycleOwner = lifecycleOwner,
        minActiveState = minActiveState,
        onEvent = { event ->
            tryHandleCommonEvent(
                event = event,
                snackbarHostState = snackbarHostState,
                resources = resources,
                navigator = navigator,
            ) || onEvent.invoke(event)
        }
    )
}

@Suppress("ComposableParametersOrdering", "IgnoredReturnValue")
@Composable
internal fun ObserveEvents(
    events: EventQueue,
    lifecycleOwner: LifecycleOwner? = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    onEvent: CoroutineScope.(Event) -> Unit,
) {
    LaunchedEffect(Unit) {
        events.flow
            .apply { if (lifecycleOwner != null) flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState) }
            .onEach { onEvent(it) }
            .launchIn(this)
    }
}

/**
 * Handles the given [event] if it is default message or navigation event.
 * @return `true` if the given event handled, otherwise `false`.
 * @see tryHandleMessageEvent
 * @see tryHandleNavigationEvent
 */
internal fun CoroutineScope.tryHandleCommonEvent(
    event: Event,
    snackbarHostState: SnackbarHostState,
    resources: Resources,
    navigator: Navigator,
): Boolean {
    return tryHandleMessageEvent(event, snackbarHostState, resources) ||
        tryHandleNavigationEvent(event, navigator)
}

/**
 * Handles the given [event] if it is a message event.
 * Uses [snackbarHostState] to show messages in snackbar.
 * @return `true` if the given event handled, otherwise `false`.
 * @see tryHandleCommonEvent
 * @see tryHandleNavigationEvent
 */
internal fun CoroutineScope.tryHandleMessageEvent(
    event: Event,
    snackbarHostState: SnackbarHostState,
    resources: Resources,
): Boolean {
    when (event) {
        is MessageEvent -> launch {
            snackbarHostState.showSnackbar(
                message = event.message.get(resources),
                snackbarStyle = event.snackbarStyle,
            )
        }
        is ErrorMessageEvent -> launch {
            snackbarHostState.showSnackbar(
                message = event.message.get(resources),
                snackbarStyle = SnackbarStyle.Error,
            )
        }
        else -> return false
    }
    return true
}

/**
 * Handles the given [event] using the given [navigator] if it is a navigation event.
 * @return `true` if the given event handled, otherwise `false`.
 * @see tryHandleCommonEvent
 * @see tryHandleMessageEvent
 */
internal fun tryHandleNavigationEvent(
    event: Event,
    navigator: Navigator,
): Boolean {
    return if (event is NavigationEvent) {
        event.navigate(navigator)
    } else {
        false
    }
}

@Preview(showBackground = true)
@Composable
private fun ObserveEventsPreview() {
    data class SimpleEvent(val value: Int) : Event

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var counter by remember { mutableStateOf(0) }
        var text by remember { mutableStateOf("[Click the button]") }
        val events = remember { EventQueue() }

        ObserveEvents(events, lifecycleOwner = null) { text = it.toString() }

        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.Black,
        )
        Row(horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = { events.offerEvent(SimpleEvent(counter++)) },
                content = { Text("Send event") },
            )
        }
    }
}
