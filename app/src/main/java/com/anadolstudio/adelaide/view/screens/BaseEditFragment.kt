package com.anadolstudio.adelaide.view.screens

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.anadolstudio.adelaide.R
import com.anadolstudio.core.dialogs.LoadingView
import com.anadolstudio.core.interfaces.EditState

abstract class BaseEditFragment : Fragment(), EditState {

    private var mLoadingView: LoadingView? = null

    fun showLoadingDialog() {
        requireActivity().let { mLoadingView = LoadingView.Base.view(it.supportFragmentManager) }
        mLoadingView?.showLoadingIndicator()
    }

    fun hideLoadingDialog() {
        mLoadingView?.hideLoadingIndicator()
    }

    override fun isReadyToApply() = true

    override fun apply(): Boolean = true

    override fun isReadyToBackClick() = true

    override fun backClick() = true

    protected var hasEditObject = false
        private set

    protected fun selectEditObject() {
        hasEditObject = true
    }

    protected fun clearEditObject() {
        hasEditObject = false
    }

    open fun nothingIsSelectedToast() = showToast(getString(R.string.edit_error_nothing_selected))

    protected fun showToast(@StringRes stringId: Int, duration: Int = Toast.LENGTH_SHORT) =
            showToast(getString(stringId), duration)

    protected fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT) =
            Toast.makeText(context, text, duration).show()
}
