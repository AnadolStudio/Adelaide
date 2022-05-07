package com.anadolstudio.adelaide.view.screens

import androidx.fragment.app.Fragment
import com.anadolstudio.core.dialogs.LoadingView
import com.anadolstudio.core.interfaces.StateListener

abstract class BaseEditFragment : Fragment(), StateListener {

    override fun isLocalApply() = false

    override fun apply(): Boolean = false

    override fun isLocalBackClick() = false

    override fun onBackClick() = false

}