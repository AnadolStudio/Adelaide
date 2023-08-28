package com.anadolstudio.adelaide.base.viewmodel

import com.anadolstudio.adelaide.navigation.NavigateData
import com.anadolstudio.core.presentation.ContentableState
import com.anadolstudio.core.viewmodel.CoreContentViewModel

abstract class BaseContentViewModel<State : ContentableState>(
        initState: State
) : CoreContentViewModel<State, NavigateData>(initState)
