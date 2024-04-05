package com.licard.b2b.app.base.presentation.event

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("IgnoredReturnValue")
@Composable
internal fun ObserveRequestPermissionEvents(
    requestPermissionEvents: StateFlow<RequestPermissionEvent?>,
    lifecycleOwner: LifecycleOwner? = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    onPermissionResult: (PermissionStatus) -> Unit = {},
) {
    val activity = LocalContext.current as Activity

    val permissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { isGranted ->
        val permission = requestPermissionEvents.value?.permission

        if (permission != null) {
            when {
                isGranted -> onPermissionResult(PermissionStatus.Granted)

                shouldShowRequestPermissionRationale(activity, permission) -> {
                    onPermissionResult(PermissionStatus.NeedsRationale)
                }

                else -> onPermissionResult(PermissionStatus.Denied)
            }
        }
    }

    LaunchedEffect(Unit) {
        requestPermissionEvents
            .apply {
                if (lifecycleOwner != null) flowWithLifecycle(
                    lifecycleOwner.lifecycle,
                    minActiveState
                )
            }
            .onEach { event -> event?.let { permissionLauncher.launch(it.permission) } }
            .launchIn(this)
    }
}
