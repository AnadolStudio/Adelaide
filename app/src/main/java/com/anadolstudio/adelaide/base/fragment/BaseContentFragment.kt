package com.anadolstudio.adelaide.base.fragment

import androidx.annotation.LayoutRes
import androidx.navigation.fragment.findNavController
import com.anadolstudio.adelaide.navigation.NavigatableDelegate
import com.anadolstudio.adelaide.navigation.NavigateData
import com.anadolstudio.core.presentation.ContentableState
import com.anadolstudio.core.presentation.Eventable
import com.anadolstudio.core.presentation.Navigatable
import com.anadolstudio.core.presentation.Renderable
import com.anadolstudio.core.presentation.fragment.CoreContentBaseFragment
import com.anadolstudio.core.viewmodel.BaseController
import com.anadolstudio.core.viewmodel.CoreContentViewModel

abstract class BaseContentFragment<
        ViewState : ContentableState,
        ViewModel : CoreContentViewModel<ViewState, NavigateData>,
        Controller : BaseController>(
        @LayoutRes layoutId: Int
) : CoreContentBaseFragment<ViewState, NavigateData, ViewModel, Controller>(layoutId), Renderable {

    override val eventableDelegate: Eventable get() = Eventable.Delegate(uiEntity = this) // TODO change delegate
    override val navigatableDelegate: Navigatable<NavigateData> get() = NavigatableDelegate(findNavController())
    override val stateMap: MutableMap<String, Int> = mutableMapOf()
}
