package com.anadolstudio.adelaide.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Suppress("InjectDispatcher")
class DispatcherProvider {
    val io: CoroutineDispatcher
        get() = Dispatchers.IO

    val default: CoroutineDispatcher
        get() = Dispatchers.Default

    val main: CoroutineDispatcher
        get() = Dispatchers.Main
}
