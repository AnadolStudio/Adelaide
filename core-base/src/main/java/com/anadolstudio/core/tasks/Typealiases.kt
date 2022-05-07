package com.anadolstudio.core.tasks

typealias RxDoMainCallback<T> = () -> T

typealias RxProgressCallback<T, ProgressData> = (ProgressListener<ProgressData>) -> T

typealias RxCallback<T> = (T) -> Unit