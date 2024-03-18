package com.anadolstudio.adelaide.event

import java.net.ConnectException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal fun Throwable.isNetworkError() = when (this) {
    is UnknownHostException,
    is ConnectException,
    is ProtocolException,
    is SocketTimeoutException -> true
    else -> false
}
