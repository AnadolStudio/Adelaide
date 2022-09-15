package com.anadolstudio.adelaide.view.screens

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.anadolstudio.adelaide.R

abstract class BaseEditFragment : Fragment() {


    fun showLoadingDialog() {
    }

    fun hideLoadingDialog() {
    }

    open fun isReadyToApply() = true

    open fun apply(): Boolean = true

    open fun isReadyToBackClick() = true

    open fun backClick() = true

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
