package com.anadolstudio.adelaide.base.bottom

import androidx.annotation.LayoutRes
import androidx.navigation.fragment.findNavController
import com.anadolstudio.adelaide.base.viewmodel.BaseActionViewModel
import com.anadolstudio.adelaide.di.SharedComponent
import com.anadolstudio.adelaide.di.getSharedModule
import com.anadolstudio.adelaide.navigation.NavigatableDelegate
import com.anadolstudio.adelaide.navigation.NavigateData
import com.anadolstudio.ui.Eventable
import com.anadolstudio.ui.Navigatable
import com.anadolstudio.ui.dialogs.bottom_sheet.CoreActionBottom
import com.anadolstudio.ui.viewmodel.BaseController

abstract class BaseActionBottom<ViewModel : BaseActionViewModel, Controller : BaseController>(
        @LayoutRes layoutId: Int
) : CoreActionBottom<Controller, NavigateData, ViewModel>(layoutId) {

    override val eventableDelegate: Eventable get() = Eventable.Delegate(uiEntity = this)
    override val navigatableDelegate: Navigatable<NavigateData> get() = NavigatableDelegate(findNavController())

    protected val viewModelFactory by lazy { getSharedComponent().viewModelsFactory() }

    protected open fun getSharedComponent(): SharedComponent = getSharedModule()
}
