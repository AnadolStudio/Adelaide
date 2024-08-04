package com.anadolstudio.adelaide.base.viewmodel

import android.os.Bundle
import androidx.core.os.bundleOf
import com.anadolstudio.adelaide.navigation.NavigateData
import com.anadolstudio.ui.viewmodel.BaseController
import com.anadolstudio.ui.viewmodel.CoreActionViewModel

abstract class BaseActionViewModel : CoreActionViewModel<NavigateData>(), BaseViewModelDelegate, BaseController {

    protected val baseViewModelDelegate: BaseViewModelDelegate = BaseViewModelDelegate.Delegate(_singleEvent)

    override fun showTodo(text: String?) = baseViewModelDelegate.showTodo(text)

    protected fun navigateTo(id: Int, args: Bundle = bundleOf()) = _navigationEvent.navigateTo(id, args)

    protected fun navigateUp() = _navigationEvent.navigateUp()

    override fun onBackClicked() = navigateUp()

}
