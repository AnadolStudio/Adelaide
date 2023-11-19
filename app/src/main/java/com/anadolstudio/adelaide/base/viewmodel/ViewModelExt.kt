package com.anadolstudio.adelaide.base.viewmodel

import android.os.Bundle
import androidx.core.os.bundleOf
import com.anadolstudio.adelaide.navigation.NavigateData
import com.anadolstudio.core.navigation.Back
import com.anadolstudio.core.navigation.NavigationEvent
import com.anadolstudio.core.navigation.Replace
import com.anadolstudio.core.viewmodel.livedata.SingleLiveEvent
import com.anadolstudio.core.viewmodel.livedata.onNext

fun SingleLiveEvent<NavigationEvent<NavigateData>>.navigateUp() = onNext(Back())

fun SingleLiveEvent<NavigationEvent<NavigateData>>.navigateTo(
         id: Int,
         args: Bundle = bundleOf()
) = onNext(Replace(NavigateData(id, args)))
