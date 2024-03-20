package com.anadolstudio.adelaide.base.fragment

import androidx.annotation.LayoutRes
import androidx.navigation.fragment.findNavController
import com.anadolstudio.adelaide.base.viewmodel.BaseContentViewModel
import com.anadolstudio.adelaide.di.SharedComponent
import com.anadolstudio.adelaide.di.getSharedModule
import com.anadolstudio.adelaide.navigation.NavigatableDelegate
import com.anadolstudio.adelaide.navigation.NavigateData
import com.anadolstudio.ui.Eventable
import com.anadolstudio.ui.Navigatable
import com.anadolstudio.ui.Renderable
import com.anadolstudio.ui.fragment.CoreContentFragment
import com.anadolstudio.ui.fragment.state_util.ViewStateDelegate
import com.anadolstudio.ui.viewmodel.BaseController

abstract class BaseContentFragment<
        ViewState : Any,
        ViewModel : BaseContentViewModel<ViewState>,
        Controller : BaseController>(
        @LayoutRes layoutId: Int
) : CoreContentFragment<ViewState, NavigateData, ViewModel, Controller>(layoutId), Renderable {

    override val viewStateDelegate: ViewStateDelegate = ViewStateDelegate()

    override val eventableDelegate: Eventable get() = Eventable.Delegate(uiEntity = this)
    override val navigatableDelegate: Navigatable<NavigateData> get() = NavigatableDelegate(findNavController())
    override val stateMap: MutableMap<String, Any?> = mutableMapOf()

    override var isStatusBarByNightMode: Boolean = true

    protected val viewModelFactory by lazy { getSharedComponent().viewModelsFactory() }

    protected open fun getSharedComponent(): SharedComponent = getSharedModule()

    override fun createViewModel(): ViewModel = createViewModelLazy().value

    protected abstract fun createViewModelLazy(): Lazy<ViewModel>

}
