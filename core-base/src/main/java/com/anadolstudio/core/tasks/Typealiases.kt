package com.anadolstudio.core.tasks

internal typealias RxDoMainCallback<T> = () -> T

internal typealias RxCallback<T> = (T) -> Unit