package com.anadolstudio.adelaide.base.fragment

import androidx.annotation.LayoutRes
import androidx.navigation.fragment.findNavController
import com.anadolstudio.adelaide.navigation.NavigatableDelegate
import com.anadolstudio.adelaide.navigation.NavigateData
import com.anadolstudio.core.presentation.Eventable
import com.anadolstudio.core.presentation.Navigatable
import com.anadolstudio.core.presentation.fragment.CoreActionBaseFragment
import com.anadolstudio.core.viewmodel.BaseController
import com.anadolstudio.core.viewmodel.CoreActionViewModel

abstract class BaseActionFragment<ViewModel : CoreActionViewModel<NavigateData>, Controller : BaseController>(
        @LayoutRes layoutId: Int
) : CoreActionBaseFragment<Controller, NavigateData, ViewModel>(layoutId) {

    // TODO change delegate
    override val eventableDelegate: Eventable get() = Eventable.Delegate(uiEntity = this)
    override val navigatableDelegate: Navigatable<NavigateData> get() = NavigatableDelegate(findNavController())

}
