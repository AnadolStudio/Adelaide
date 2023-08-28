package com.anadolstudio.adelaide.base.viewmodel

import com.anadolstudio.adelaide.navigation.NavigateData
import com.anadolstudio.core.livedata.SingleLiveEvent
import com.anadolstudio.core.livedata.onNext
import com.anadolstudio.core.navigation.Add
import com.anadolstudio.core.navigation.Back
import com.anadolstudio.core.navigation.NavigationEvent

fun SingleLiveEvent<NavigationEvent<NavigateData>>.navigateUp() = onNext(Back())

fun SingleLiveEvent<NavigationEvent<NavigateData>>.navigateTo(
         id: Int,
         args: Map<String, Any> = emptyMap()
) = onNext(Add(NavigateData(id, args)))
