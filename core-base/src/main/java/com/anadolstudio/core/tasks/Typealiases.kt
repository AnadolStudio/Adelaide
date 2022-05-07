package com.anadolstudio.core.tasks

typealias RxDoMainCallback<T> = () -> T

typealias RxProgressCallback<T> = (ProgressListener) -> T

typealias RxCallback<T> = (T) -> Unit