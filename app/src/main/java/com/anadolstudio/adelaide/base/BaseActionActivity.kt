package com.anadolstudio.adelaide.base

import androidx.annotation.IdRes
import androidx.navigation.findNavController
import com.anadolstudio.adelaide.base.viewmodel.BaseActionViewModel
import com.anadolstudio.adelaide.di.SharedComponent
import com.anadolstudio.adelaide.di.getSharedModule
import com.anadolstudio.adelaide.navigation.NavigatableDelegate
import com.anadolstudio.adelaide.navigation.NavigateData
import com.anadolstudio.ui.Navigatable
import com.anadolstudio.ui.activity.CoreActionActivity
import com.anadolstudio.ui.viewmodel.BaseController

abstract class BaseActionActivity<
        ViewModel : BaseActionViewModel,
        Controller : BaseController>(
        @IdRes val navigateContainerId: Int,
) : CoreActionActivity<Controller, NavigateData, ViewModel>() {

    override val navigatableDelegate: Navigatable<NavigateData> get() = NavigatableDelegate(findNavController(navigateContainerId))

    protected val viewModelFactory by lazy { getSharedComponent().viewModelsFactory() }

    protected open fun getSharedComponent(): SharedComponent = getSharedModule()

    override fun createViewModel(): ViewModel = createViewModelLazy().value

    protected abstract fun createViewModelLazy(): Lazy<ViewModel>
}
