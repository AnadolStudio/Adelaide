package com.anadolstudio.adelaide.util.livedata

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData

@Composable
fun <T> LiveData<T>.observeState(): State<T> = observeAsState(requireNotNull(value))
