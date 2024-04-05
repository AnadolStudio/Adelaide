package com.licard.b2b.app.base.presentation.event

import com.anadolstudio.adelaide.event.Event

internal class RequestPermissionEvent(val permission: String) : Event

internal sealed class PermissionStatus {
    object Granted : PermissionStatus()
    object Denied : PermissionStatus()
    object NeedsRationale : PermissionStatus()
}
